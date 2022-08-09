package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.ParserNotImplemented;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.*;
import xyz.lebster.core.node.declaration.*;
import xyz.lebster.core.node.expression.*;
import xyz.lebster.core.node.expression.ClassExpression.ClassConstructorNode;
import xyz.lebster.core.node.expression.ClassExpression.ClassFieldNode;
import xyz.lebster.core.node.expression.ClassExpression.ClassMethodNode;
import xyz.lebster.core.node.expression.UpdateExpression.UpdateOp;
import xyz.lebster.core.node.expression.literal.*;
import xyz.lebster.core.node.statement.*;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.number.NumberValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.*;

import static xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp;
import static xyz.lebster.core.parser.Associativity.Left;
import static xyz.lebster.core.parser.Associativity.Right;

public final class Parser {
	private final String sourceText;
	private ParserState state;
	private ParserState saved = null;
	private boolean hasConsumedSeparator = false;

	public Parser(String sourceText, Token[] tokens) {
		this.sourceText = sourceText;
		this.state = new ParserState(tokens);
	}

	public Parser(String sourceText) throws SyntaxError {
		this(sourceText, Lexer.tokenize(sourceText));
	}

	private void save() {
		this.saved = state.copy();
	}

	private void load() {
		if (this.saved == null) throw new IllegalStateException("Attempting to load invalid ParseState");
		this.state = saved;
		this.saved = null;
	}

	public Program parse() throws SyntaxError, CannotParse {
		final Program program = new Program();
		populateAppendableNode(program, TokenType.EOF);
		return program;
	}

	private <T extends AppendableNode> void populateAppendableNode(T root, TokenType... end) throws CannotParse, SyntaxError {
		boolean isFirstStatement = true;
		while (state.index < state.tokens.length && !state.is(end)) {
			if (isFirstStatement) {
				isFirstStatement = false;
				consumeAllSeparators();
			} else if (didConsumeSeparator()) {
				consumeAllSeparators();
			} else {
				requireAtLeastOneSeparator();
			}

			if (state.is(end)) break;
			root.append(parseAny());
		}
	}

	private void requireAtLeastOneSeparator() throws SyntaxError {
		boolean done = false;
		while (!state.is(TokenType.EOF)) {
			if (state.optional(TokenType.LineTerminator, TokenType.Semicolon)) {
				done = true;
			} else {
				if (done) {
					return;
				} else {
					state.unexpected();
				}
			}
		}
	}

	private boolean didConsumeSeparator() {
		if (hasConsumedSeparator) {
			hasConsumedSeparator = false;
			return true;
		} else {
			return false;
		}
	}

	private void consumeAllSeparators() {
		while (!state.is(TokenType.EOF) && state.is(TokenType.LineTerminator, TokenType.Semicolon)) {

			hasConsumedSeparator = true;
			state.consume();
		}
	}

	private void consumeAllLineTerminators() {
		while (!state.is(TokenType.EOF) && state.is(TokenType.LineTerminator)) {

			hasConsumedSeparator = true;
			state.consume();
		}
	}

	private Statement parseLine() throws SyntaxError, CannotParse {
		consumeAllLineTerminators();
		final Statement result = parseAny();
		state.consumeAll(TokenType.Semicolon);
		consumeAllLineTerminators();
		return result;
	}

	private Statement parseAny() throws SyntaxError, CannotParse {
		if (state.token.matchDeclaration()) {
			return parseDeclaration();
		} else if (state.token.matchPrimaryExpression() || state.token.matchStatement()) {
			return parseStatementOrExpression();
		} else {
			throw new CannotParse(state.token);
		}
	}

	private BlockStatement parseBlockStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.LBrace);
		final BlockStatement result = new BlockStatement();
		populateAppendableNode(result, TokenType.RBrace);
		state.require(TokenType.RBrace);
		return result;
	}

	private Statement parseStatementOrExpression() throws SyntaxError, CannotParse {
		return switch (state.token.type) {
			case Import, Export -> throw new ParserNotImplemented(position(), "Parsing import / export statements");
			case With -> throw new ParserNotImplemented(position(), "Parsing with statements");

			case Function -> parseFunctionDeclaration();
			case Semicolon -> new EmptyStatement();
			case LBrace -> parseBlockStatement();
			case While -> parseWhileStatement();
			case Do -> parseDoWhileStatement();
			case For -> parseForStatement();
			case If -> parseIfStatement();
			case Try -> parseTryStatement();
			case Switch -> parseSwitchStatement();
			case Super -> parseSuperCall();
			case Break -> parseBreakStatement();
			case Continue -> parseContinueStatement();

			case Return -> {
				state.consume();
				// FIXME: Proper automatic semicolon insertion
				yield new ReturnStatement(state.token.matchPrimaryExpression() ? parseExpression() : null);
			}

			case Throw -> {
				state.consume();
				yield new ThrowStatement(parseExpression());
			}

			default -> {
				if (state.token.matchPrimaryExpression()) {
					yield new ExpressionStatement(parseExpression());
				} else {
					throw new CannotParse(state.token, "Statement");
				}
			}
		};
	}

	private Statement parseSwitchStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Switch);
		consumeAllLineTerminators();
		state.require(TokenType.LParen);
		consumeAllLineTerminators();
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
		consumeAllLineTerminators();
		state.require(TokenType.LBrace);
		final boolean old_inBreakContext = state.inBreakContext;
		state.inBreakContext = true;
		try {
			return new SwitchStatement(expression, parseSwitchCases());
		} finally {
			state.inBreakContext = old_inBreakContext;
		}
	}

	private SwitchCase[] parseSwitchCases() throws SyntaxError, CannotParse {
		final List<SwitchCase> cases = new ArrayList<>();
		while (!(state.is(TokenType.RBrace, TokenType.EOF))) {
			consumeAllLineTerminators();
			if (state.optional(TokenType.Default)) {
				state.require(TokenType.Colon);
				cases.add(new SwitchCase(null, parseSwitchCaseStatements()));
			} else if (state.optional(TokenType.Case)) {
				final Expression test = parseExpression();
				state.require(TokenType.Colon);
				cases.add(new SwitchCase(test, parseSwitchCaseStatements()));
			} else {
				state.expected(TokenType.Case);
			}

			consumeAllSeparators();
		}

		state.require(TokenType.RBrace);
		return cases.toArray(new SwitchCase[0]);
	}

	private Statement[] parseSwitchCaseStatements() throws CannotParse, SyntaxError {
		boolean isFirstStatement = true;
		final List<Statement> statementList = new ArrayList<>();
		while (true) {
			if (isFirstStatement) {
				isFirstStatement = false;
				consumeAllSeparators();
			} else if (didConsumeSeparator()) {
				consumeAllSeparators();
			} else {
				requireAtLeastOneSeparator();
			}

			if (state.is(TokenType.Case, TokenType.Default, TokenType.RBrace, TokenType.EOF))
				break;

			statementList.add(parseAny());
		}

		return statementList.toArray(new Statement[0]);
	}

	private ContinueStatement parseContinueStatement() throws SyntaxError {
		if (!state.inContinueContext) throw new SyntaxError("Illegal `continue` statement", position());
		state.consume();
		return new ContinueStatement();
	}

	private BreakStatement parseBreakStatement() throws SyntaxError {
		if (!state.inBreakContext) throw new SyntaxError("Illegal `break` statement", position());
		state.consume();
		return new BreakStatement();
	}

	private BlockStatement parseFunctionBody() throws CannotParse, SyntaxError {
		final boolean old_inBreakContext = state.inBreakContext;
		final boolean old_inContinueContext = state.inContinueContext;
		state.inBreakContext = false;
		state.inContinueContext = false;
		try {
			return parseBlockStatement();
		} finally {
			state.inBreakContext = old_inBreakContext;
			state.inContinueContext = old_inContinueContext;
		}
	}

	private Statement parseContextualStatement(boolean inBreakContext, boolean inContinueContext) throws CannotParse, SyntaxError {
		final boolean old_inBreakContext = state.inBreakContext;
		final boolean old_inContinueContext = state.inContinueContext;
		state.inBreakContext = inBreakContext;
		state.inContinueContext = inContinueContext;
		try {
			return parseLine();
		} finally {
			state.inBreakContext = old_inBreakContext;
			state.inContinueContext = old_inContinueContext;
		}
	}

	private ForOfStatement parseForOfStatement(Assignable left) throws SyntaxError, CannotParse {
		state.require(TokenType.Identifier, "of");
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForOfStatement(left, expression, body);
	}

	private ForInStatement parseForInStatement(Assignable left) throws SyntaxError, CannotParse {
		state.require(TokenType.In);
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForInStatement(left, expression, body);
	}

	private Statement parseForStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.For);
		consumeAllLineTerminators();
		state.require(TokenType.LParen);
		consumeAllLineTerminators();

		Statement init = null;
		if (!state.is(TokenType.Semicolon)) {
			if (state.token.matchVariableDeclaration()) {
				// TODO: for_loop_variable_declaration
				final VariableDeclaration declaration = parseVariableDeclaration(/* true */);
				if (state.match(TokenType.Identifier, "of")) {
					// for ( LetOrConst ForBinding of AssignmentExpression ) Statement
					if (declaration.declarations().length != 1) throw new SyntaxError("Invalid left-hand side in for-of loop: Must have a single binding.", position());
					if (declaration.declarations()[0].init() != null) throw new SyntaxError("for-of loop variable declaration may not have an init.", position());

					return parseForOfStatement(new BindingPattern(declaration));
				} else if (state.is(TokenType.In)) {
					// for ( LetOrConst ForBinding in Expression ) Statement
					if (declaration.declarations().length != 1) throw new SyntaxError("Invalid left-hand side in for-in loop: Must have a single binding.", position());
					if (declaration.declarations()[0].init() != null) throw new SyntaxError("for-in loop variable declaration may not have an init.", position());

					return parseForInStatement(new BindingPattern(declaration));
				} else {
					init = declaration;
				}
			} else if (state.token.matchPrimaryExpression()) {
				final Expression expression = parseExpression(0, Associativity.Right, Collections.singleton(TokenType.In));
				if (state.match(TokenType.Identifier, "of")) {
					return parseForOfStatement(ensureLHS(expression, "Invalid left-hand side in for-loop"));
				} else if (state.is(TokenType.In)) {
					return parseForInStatement(ensureLHS(expression, "Invalid left-hand side in for-loop"));
				} else {
					init = new ExpressionStatement(expression);
				}
			} else {
				state.unexpected();
			}
		}

		state.require(TokenType.Semicolon);
		consumeAllLineTerminators();

		final Expression test = state.token.matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.Semicolon);
		consumeAllLineTerminators();

		final Expression update = state.token.matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.RParen);

		final Statement body = parseContextualStatement(true, true);
		return new ForStatement(init, test, update, body);
	}

	private Expression parseWhileCondition() throws SyntaxError, CannotParse {
		state.require(TokenType.While);
		consumeAllLineTerminators();
		state.require(TokenType.LParen);
		consumeAllLineTerminators();
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		return condition;
	}

	private WhileStatement parseWhileStatement() throws SyntaxError, CannotParse {
		final Expression condition = parseWhileCondition();
		final Statement body = parseContextualStatement(true, true);
		return new WhileStatement(condition, body);
	}

	private DoWhileStatement parseDoWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Do);
		final Statement body = parseContextualStatement(true, true);
		final Expression condition = parseWhileCondition();
		return new DoWhileStatement(body, condition);
	}

	private TryStatement parseTryStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Try);
		final BlockStatement body = parseBlockStatement();
		consumeAllLineTerminators();

		String catchParameter = null;
		BlockStatement catchBody = null;
		final boolean hasCatch = state.optional(TokenType.Catch);
		if (hasCatch) {
			if (state.optional(TokenType.LParen)) {
				catchParameter = state.require(TokenType.Identifier);
				state.require(TokenType.RParen);
			}

			catchBody = parseBlockStatement();
			consumeAllLineTerminators();
		}

		BlockStatement finallyBody = null;
		if (state.optional(TokenType.Finally)) {
			finallyBody = parseBlockStatement();
		} else if (!hasCatch) {
			throw new SyntaxError("Missing catch or finally after try", position());
		}

		return new TryStatement(body, catchParameter, catchBody, finallyBody);
	}

	private IfStatement parseIfStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.If);
		consumeAllLineTerminators();
		state.require(TokenType.LParen);
		consumeAllLineTerminators();
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement consequence = parseLine();
		final Statement elseStatement = state.is(TokenType.Else) ? parseElseStatement() : null;
		return new IfStatement(condition, consequence, elseStatement);
	}

	public Statement parseElseStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Else);
		return parseLine();
	}

	private Declaration parseDeclaration() throws SyntaxError, CannotParse {
		return switch (state.token.type) {
			case Let, Var, Const -> parseVariableDeclaration();
			case Function -> parseFunctionDeclaration();
			case Class -> parseClassDeclaration();
			default -> throw new CannotParse(state.token, "Declaration");
		};
	}

	private SourcePosition position() {
		return state.token.position;
	}

	private SourceRange range(SourcePosition start) {
		return new SourceRange(sourceText, start, position());
	}

	private VariableDeclaration parseVariableDeclaration() throws SyntaxError, CannotParse {
		// TODO: Missing init in 'const' declaration
		final var kind = switch (state.token.type) {
			case Var -> VariableDeclaration.Kind.Var;
			case Let -> VariableDeclaration.Kind.Let;
			case Const -> VariableDeclaration.Kind.Const;
			default -> throw new SyntaxError("Unexpected token " + state.token, state.token.position);
		};

		state.consume();

		final List<VariableDeclarator> declarators = new ArrayList<>();
		while (true) {
			declarators.add(parseVariableDeclarator());
			consumeAllLineTerminators();
			if (!state.is(TokenType.Comma)) break;
			state.consume();
			consumeAllLineTerminators();
		}

		return new VariableDeclaration(kind, declarators.toArray(new VariableDeclarator[0]));
	}

	private VariableDeclarator parseVariableDeclarator() throws SyntaxError, CannotParse {
		final SourcePosition declaratorStart = position();
		final AssignmentTarget lhs = parseAssignmentTarget();
		final Expression value = state.optional(TokenType.Equals) ? parseSpecAssignmentExpression() : null;
		return new VariableDeclarator(lhs, value, range(declaratorStart));
	}

	private AssignmentTarget parseAssignmentTarget() throws SyntaxError, CannotParse {
		if (state.optional(TokenType.LBrace)) {
			consumeAllLineTerminators();

			final Map<Expression, AssignmentTarget> pairs = new HashMap<>();
			StringValue restName = null;
			while (!state.is(TokenType.RBrace)) {
				if (state.optional(TokenType.DotDotDot)) {
					consumeAllLineTerminators();
					if (state.token.matchIdentifierName()) {
						restName = new StringValue(state.consume().value);
						consumeAllLineTerminators();
						if (!state.optional(TokenType.Comma)) break;
						consumeAllLineTerminators();
						continue;
					} else {
						throw new SyntaxError("`...` must be followed by an identifier in declaration contexts", position());
					}
				}

				if (state.token.matchIdentifierName()) {
					final StringLiteral key = parseAsStringLiteral();
					consumeAllLineTerminators();

					if (state.optional(TokenType.Colon)) {
						consumeAllLineTerminators();
						pairs.put(key, parseAssignmentTarget());
					} else {
						pairs.put(key, new IdentifierExpression(key.value()));
					}
				} else {
					pairs.put(parseComputedKeyExpression(), parseAssignmentTarget());
				}

				consumeAllLineTerminators();
				if (!state.optional(TokenType.Comma)) break;
				consumeAllLineTerminators();
			}

			consumeAllLineTerminators();
			if (state.is(TokenType.Equals)) throw new ParserNotImplemented(position(), "Parsing destructuring assignment targets with defaults");
			state.require(TokenType.RBrace);
			return new ObjectDestructuring(pairs, restName);
		} else if (state.optional(TokenType.LBracket)) {
			final ArrayList<AssignmentTarget> children = new ArrayList<>();
			AssignmentTarget restTarget = null;

			consumeAllLineTerminators();
			while (true) {
				consumeAllLineTerminators();
				if (state.optional(TokenType.Comma)) {
					children.add(null);
				} else if (state.optional(TokenType.DotDotDot)) {
					consumeAllLineTerminators();
					restTarget = parseAssignmentTarget();
					consumeAllLineTerminators();
					if (state.optional(TokenType.Comma)) throw new SyntaxError("Rest element must be last element", position());
					break;
				} else {
					children.add(parseAssignmentTarget());
					consumeAllLineTerminators();
					if (!state.optional(TokenType.Comma)) break;
				}
			}

			if (state.is(TokenType.Equals)) throw new ParserNotImplemented(position(), "Parsing destructuring assignment targets with defaults");
			state.require(TokenType.RBracket);
			return new ArrayDestructuring(restTarget, children.toArray(new AssignmentTarget[0]));
		} else if (state.token.matchIdentifierName()) {
			return new IdentifierExpression(state.consume().value);
		} else {
			state.unexpected();
			return null;
		}
	}

	private Expression parseComputedKeyExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBracket);
		final Expression keyExpression = parseExpression();
		state.require(TokenType.RBracket);
		consumeAllLineTerminators();
		state.require(TokenType.Colon);
		consumeAllLineTerminators();
		return keyExpression;
	}

	private FunctionParameters parseFunctionParameters(boolean expectLParen) throws SyntaxError, CannotParse {
		if (expectLParen) state.require(TokenType.LParen);

		final FunctionParameters result = new FunctionParameters();
		consumeAllLineTerminators();
		while (!state.is(TokenType.RParen)) {
			consumeAllLineTerminators();
			if (state.optional(TokenType.DotDotDot)) {
				// Note: Rest parameter may not have a default initializer
				consumeAllLineTerminators();
				result.rest = parseAssignmentTarget();
				consumeAllLineTerminators();
				if (state.optional(TokenType.Comma)) throw new SyntaxError("Rest parameter must be last formal parameter", position());
				break;
			} else {
				final AssignmentTarget target = parseAssignmentTarget();
				consumeAllLineTerminators();
				if (state.optional(TokenType.Equals)) {
					final Expression defaultExpression = parseSpecAssignmentExpression();
					result.addWithDefault(target, defaultExpression);
				} else {
					result.add(target);
				}
			}

			consumeAllLineTerminators();
			if (!state.optional(TokenType.Comma)) break;
		}

		state.require(TokenType.RParen);
		return result;
	}

	private FunctionDeclaration parseFunctionDeclaration() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		if (state.is(TokenType.Star)) throw new ParserNotImplemented(position(), "Generator function declarations");
		consumeAllLineTerminators();
		if (!state.is(TokenType.Identifier)) {
			throw new SyntaxError("Function declarations require a function name", position());
		}

		final String name = state.consume().value;
		consumeAllLineTerminators();
		final FunctionParameters parameters = parseFunctionParameters(true);
		consumeAllLineTerminators();
		return new FunctionDeclaration(parseFunctionBody(), name, parameters);
	}

	private FunctionExpression parseFunctionExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		if (state.is(TokenType.Star)) throw new ParserNotImplemented(position(), "Generator function expressions");
		final Token potentialName = state.accept(TokenType.Identifier);
		final String name = potentialName == null ? null : potentialName.value;
		final FunctionParameters parameters = parseFunctionParameters(true);
		return new FunctionExpression(parseFunctionBody(), name, parameters);
	}

	private ExpressionList parseExpressionList(boolean expectParens, boolean canHaveEmpty) throws SyntaxError, CannotParse {
		final ExpressionList result = new ExpressionList(canHaveEmpty);
		if (expectParens) state.require(TokenType.LParen);
		consumeAllLineTerminators();

		while (state.token.matchPrimaryExpression() || state.is(TokenType.DotDotDot, TokenType.Comma)) {
			consumeAllLineTerminators();
			if (state.optional(TokenType.DotDotDot)) {
				result.addSpreadExpression(parseSpecAssignmentExpression());
			} else if (canHaveEmpty && state.optional(TokenType.Comma)) {
				result.addEmpty();
				consumeAllLineTerminators();
				continue;
			} else {
				result.addSingleExpression(parseSpecAssignmentExpression());
			}

			consumeAllLineTerminators();
			if (!state.optional(TokenType.Comma)) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		if (expectParens) state.require(TokenType.RParen);
		return result;
	}

	private CallExpression parseCallExpression(Expression left) throws SyntaxError, CannotParse {
		final ExpressionList arguments = parseExpressionList(false, false);
		state.require(TokenType.RParen);
		return new CallExpression(left, arguments);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-UnaryExpression")
	private Expression parseUnaryPrefixedExpression() throws SyntaxError, CannotParse {
		final Token token = state.consume();
		final Associativity assoc = token.associativity();
		final int minPrecedence = token.precedence();

		final UnaryExpression.UnaryOp op = switch (token.type) {
			case Delete -> UnaryExpression.UnaryOp.Delete;
			case Void -> UnaryExpression.UnaryOp.Void;
			case Typeof -> UnaryExpression.UnaryOp.Typeof;
			case Plus -> UnaryExpression.UnaryOp.UnaryPlus;
			case Minus -> UnaryExpression.UnaryOp.UnaryMinus;
			case Tilde -> UnaryExpression.UnaryOp.BitwiseNot;
			case Bang -> UnaryExpression.UnaryOp.LogicalNot;
			case Await -> UnaryExpression.UnaryOp.Await;
			default -> throw new CannotParse(token, "Unary Operator");
		};

		return new UnaryExpression(parseExpression(minPrecedence, assoc), op);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-UpdateExpression")
	private UpdateExpression parsePrefixedUpdateExpression() throws CannotParse, SyntaxError {
		final Token token = state.consume();
		final Associativity assoc = token.associativity();
		final int minPrecedence = token.precedence();

		final UpdateOp op = switch (token.type) {
			case PlusPlus -> UpdateOp.PreIncrement;
			case MinusMinus -> UpdateOp.PreDecrement;
			default -> throw new CannotParse(token, "Prefix update Operator");
		};

		return new UpdateExpression(ensureLHS(parseExpression(minPrecedence, assoc), UpdateExpression.invalidPreLHS), op);
	}

	private Expression parseSpecAssignmentExpression() throws CannotParse, SyntaxError {
		return parseExpression(1, Left, new HashSet<>());
	}

	private Expression parseExpression() throws SyntaxError, CannotParse {
		return parseExpression(0, Left, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		return parseExpression(minPrecedence, assoc, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc, Set<TokenType> forbidden) throws SyntaxError, CannotParse {
		Expression latestExpr = parsePrimaryExpression();
		consumeAllLineTerminators();

		while (state.token.matchSecondaryExpression(forbidden)) {
			final int newPrecedence = state.token.precedence();

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left) break;

			final Associativity newAssoc = state.token.associativity();
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
			consumeAllLineTerminators();
		}

		return latestExpr;
	}

	private LeftHandSideExpression ensureLHS(Expression expression, String failureMessage) throws SyntaxError {
		if (expression instanceof final LeftHandSideExpression leftHandSideExpression) {
			return leftHandSideExpression;
		} else {
			throw new SyntaxError(failureMessage, position());
		}
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		final Token token = state.consume();
		consumeAllLineTerminators();

		return switch (token.type) {
			case Plus, Minus, Star, Slash, Percent, Exponent, Pipe, Ampersand, Caret, LeftShift, RightShift, UnsignedRightShift ->
				new BinaryExpression(left, parseExpression(minPrecedence, assoc), token.getBinaryOp());
			case StrictEqual, LooseEqual, StrictNotEqual, NotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), token.getEqualityOp());
			case LogicalOr, LogicalAnd, NullishCoalescing -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), token.getLogicOp());
			case LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, InstanceOf -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), token.getRelationalOp());
			case MinusMinus, PlusPlus -> new UpdateExpression(ensureLHS(left, UpdateExpression.invalidPostLHS), token.getUpdateOp());
			case Equals, LogicalAndEquals, LogicalOrEquals, NullishCoalescingEquals, MultiplyEquals,
				DivideEquals, PercentEquals, PlusEquals, MinusEquals, LeftShiftEquals, RightShiftEquals,
				UnsignedRightShiftEquals, AmpersandEquals, CaretEquals, PipeEquals, ExponentEquals ->
				new AssignmentExpression(ensureAssignable(left, token.getAssignmentOp()), parseExpression(minPrecedence, assoc), token.getAssignmentOp());

			case QuestionMark -> parseConditionalExpression(left);
			case LParen -> parseCallExpression(left);
			case Period -> parseNonComputedMemberExpression(left);
			case LBracket -> parseComputedMemberExpression(left);
			case Comma -> new SequenceExpression(left, parseExpression());

			case OptionalChain -> throw new ParserNotImplemented(position(), "Parsing optional chaining");
			default -> throw new CannotParse(token, "SecondaryExpression");
		};
	}

	private MemberExpression parseComputedMemberExpression(Expression left) throws SyntaxError, CannotParse {
		final Expression prop = parseExpression();
		state.require(TokenType.RBracket);
		return new MemberExpression(left, prop, true);
	}

	private MemberExpression parseNonComputedMemberExpression(Expression left) throws SyntaxError {
		if (!state.token.matchIdentifierName()) state.expected("IdentifierName");
		return new MemberExpression(left, parseAsStringLiteral(), false);
	}

	private Assignable ensureAssignable(Expression left_expr, AssignmentOp op) throws SyntaxError, CannotParse {
		if (left_expr instanceof final Assignable assignable) {
			if (left_expr instanceof LeftHandSideExpression || op == AssignmentOp.Assign) {
				return assignable;
			}
		} else if (op == AssignmentOp.Assign && (left_expr instanceof ArrayExpression || left_expr instanceof ObjectExpression)) {
			// left_expr is a destructuring pattern we mis-parsed as an array / object literal
			// TODO: Convert to DestructuringAssignmentTarget manually, rather than re-parsing the source
			return new Parser(left_expr.range().getText()).parseAssignmentTarget();
		}

		throw new SyntaxError(AssignmentExpression.invalidLHS, position());
	}

	private Expression parseConditionalExpression(Expression test) throws CannotParse, SyntaxError {
		final Expression left = parseExpression(2, Right);
		consumeAllLineTerminators();
		state.require(TokenType.Colon);
		consumeAllLineTerminators();
		final Expression right = parseExpression(2, Right);
		return new ConditionalExpression(test, left, right);
	}

	private Expression parsePrimaryExpression() throws SyntaxError, CannotParse {
		if (state.token.matchPrefixedUpdateExpression()) {
			return parsePrefixedUpdateExpression();
		} else if (state.token.matchUnaryPrefixedExpression()) {
			return parseUnaryPrefixedExpression();
		}

		return switch (state.token.type) {
			case Await -> throw new ParserNotImplemented(position(), "Parsing `await` expressions");
			case Async -> throw new ParserNotImplemented(position(), "Parsing `async` functions");
			case BigIntLiteral -> throw new ParserNotImplemented(position(), "Parsing BigIntLiterals");
			case Super -> throw new ParserNotImplemented(position(), "Parsing Super property access");

			case Class -> parseClassExpression();
			case Function -> parseFunctionExpression();
			case LBracket -> parseArrayExpression();
			case LBrace -> parseObjectExpression();
			case TemplateStart -> parseTemplateLiteral();
			case New -> parseNewExpression();
			case LParen -> parseParenthesizedExpressionOrArrowFunctionExpression();
			case Identifier -> parseIdentifierExpressionOrArrowFunctionExpression();

			case RegexpLiteral -> parseRegexpLiteral();
			case StringLiteral -> parseAsStringLiteral();
			case NumericLiteral -> parseNumericLiteral(false);
			case Period -> {
				state.consume();
				yield parseNumericLiteral(true);
			}

			case True -> {
				state.consume();
				yield new BooleanLiteral(BooleanValue.TRUE);
			}

			case False -> {
				state.consume();
				yield new BooleanLiteral(BooleanValue.FALSE);
			}

			case This -> {
				state.consume();
				yield new ThisKeyword();
			}

			case Null -> {
				state.consume();
				yield new NullLiteral();
			}

			case TemplateExpressionEnd -> throw new SyntaxError("Unexpected end of template expression", position());
			default -> throw new CannotParse(state.token, "PrimaryExpression");
		};
	}

	private NumericLiteral parseNumericLiteral(boolean leadingDecimal) throws SyntaxError {
		final String value = (leadingDecimal ? "." : "") + state.require(TokenType.NumericLiteral);
		return new NumericLiteral(new NumberValue(Double.parseDouble(value)));
	}

	private RegExpLiteral parseRegexpLiteral() throws SyntaxError {
		final String pattern = state.require(TokenType.RegexpLiteral);
		return new RegExpLiteral(pattern);
	}

	private Expression parseIdentifierExpressionOrArrowFunctionExpression() throws CannotParse, SyntaxError {
		final var identifier = new IdentifierExpression(state.consume().value);
		if (state.is(TokenType.TemplateStart)) throw new ParserNotImplemented(position(), "Parsing tagged template literals");
		if (!state.optional(TokenType.Arrow)) return identifier;
		return parseArrowFunctionBody(new FunctionParameters(identifier));
	}

	private Expression parseParenthesizedExpressionOrArrowFunctionExpression() throws CannotParse, SyntaxError {
		final SourcePosition start = state.consume().position;

		consumeAllLineTerminators();
		if (state.is(TokenType.RParen, TokenType.Identifier, TokenType.DotDotDot, TokenType.LBrace, TokenType.LBracket)) {
			final ArrowFunctionExpression result = tryParseArrowFunctionExpression();
			if (result != null) return result;
		}

		consumeAllLineTerminators();
		final Expression expression = parseExpression();
		consumeAllLineTerminators();
		state.require(TokenType.RParen);
		return new ParenthesizedExpression(expression, range(start));
	}

	private StringLiteral parseAsStringLiteral() {
		return new StringLiteral(new StringValue(state.consume().value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-new-operator")
	private NewExpression parseNewExpression() throws SyntaxError, CannotParse {
		final Token newOperator = state.consume();
		consumeAllLineTerminators();
		if (state.is(TokenType.Period)) throw new ParserNotImplemented(position(), "Parsing new.target");
		final Expression constructExpr = parseExpression(newOperator.precedence(), newOperator.associativity(), Collections.singleton(TokenType.LParen));
		final boolean hasArguments = state.is(TokenType.LParen);
		final ExpressionList arguments = hasArguments ? parseExpressionList(true, false) : null;
		return new NewExpression(constructExpr, arguments);
	}

	// FIXME: Correctly handle super in all cases
	private SuperCallStatement parseSuperCall() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(TokenType.Super);
		final ExpressionList args = parseExpressionList(true, false);
		return new SuperCallStatement(args, range(start));
	}

	private ClassExpression parseClassExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(TokenType.Class);
		consumeAllLineTerminators();
		final Token potentialName = state.accept(TokenType.Identifier);
		final String className = potentialName == null ? null : potentialName.value;
		consumeAllLineTerminators();
		final Expression heritage = parseClassHeritage();
		consumeAllLineTerminators();
		return parseClassBody(start, className, heritage);
	}

	private ObjectExpression parseObjectExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(TokenType.LBrace);
		consumeAllLineTerminators();
		final ArrayList<ObjectExpression.ObjectEntryNode> entries = new ArrayList<>();

		// FIXME:
		// 		- Methods { a() { alert(1) } }
		// 		- Getters / Setters { get a() { return Math.random() } }
		boolean couldBeGetterSetter = false;
		while (!state.is(TokenType.RBrace)) {
			if (state.optional(TokenType.DotDotDot)) {
				consumeAllLineTerminators();
				entries.add(ObjectExpression.spreadEntry(parseSpecAssignmentExpression()));
				consumeAllLineTerminators();
				if (!state.optional(TokenType.Comma)) break;
				consumeAllLineTerminators();
				continue;
			}

			// TODO: Remove duplication with parseClassElementName
			boolean isIdentifier = state.token.matchIdentifierName();
			if (isIdentifier || state.is(TokenType.NumericLiteral, TokenType.StringLiteral)) {
				final String propertyName = state.consume().value;
				consumeAllLineTerminators();

				if (state.is(TokenType.LParen)) throw new ParserNotImplemented(position(), "Parsing object literal methods");

				if (isIdentifier && !state.is(TokenType.Colon)) {
					entries.add(ObjectExpression.shorthandEntry(new StringValue(propertyName)));
					couldBeGetterSetter = propertyName.equals("get") || propertyName.equals("set");
				} else {
					state.require(TokenType.Colon);
					consumeAllLineTerminators();
					entries.add(ObjectExpression.staticEntry(new StringValue(propertyName), parseSpecAssignmentExpression()));
				}
			} else {
				state.require(TokenType.LBracket);
				final Expression keyExpression = parseExpression();
				state.require(TokenType.RBracket);
				consumeAllLineTerminators();
				if (state.is(TokenType.LParen)) throw new ParserNotImplemented(position(), "Parsing object literal methods");
				state.require(TokenType.Colon);
				consumeAllLineTerminators();
				entries.add(ObjectExpression.computedKeyEntry(keyExpression, parseSpecAssignmentExpression()));
			}

			consumeAllLineTerminators();
			if (!state.optional(TokenType.Comma)) break;
			consumeAllLineTerminators();
		}

		if (couldBeGetterSetter) {
			throw new ParserNotImplemented(position(), "Object literal getter & setter syntax");
		}

		consumeAllLineTerminators();
		state.require(TokenType.RBrace);

		return new ObjectExpression(range(start), entries);
	}

	private ClassExpression parseClassBody(SourcePosition start, String className, Expression heritage) throws SyntaxError, CannotParse {
		ClassConstructorNode constructor = null;
		final List<ClassMethodNode> methods = new ArrayList<>();
		final List<ClassFieldNode> fields = new ArrayList<>();
		state.require(TokenType.LBrace);
		consumeAllSeparators();

		final boolean isDerived = heritage != null;

		while (!state.is(TokenType.RBrace)) {
			consumeAllSeparators();
			final boolean isStatic = state.optional(TokenType.Static);
			if (isStatic) throw new ParserNotImplemented(position(), "Parsing class `static` methods / fields");

			if (state.optional(TokenType.LBrace)) {
				// TODO: Class static blocks
				throw new ParserNotImplemented(position(), "Parsing class `static` blocks");
			} else if (state.optional(TokenType.Star)) {
				throw new ParserNotImplemented(position(), "Parsing class generator methods");
			} else if (state.token.matchClassElementName()) {
				final SourcePosition elementStart = position();
				final Token name = state.consume();
				consumeAllLineTerminators();

				// FieldDefinition
				if (state.optional(TokenType.Equals)) {
					consumeAllLineTerminators();
					final Expression initializer = parseSpecAssignmentExpression();
					consumeAllLineTerminators();
					fields.add(new ClassFieldNode(name.value, initializer, range(elementStart)));
				} else {
					if (name.type == TokenType.Async) {
						throw new ParserNotImplemented(position(), "Parsing class async methods");
					} else if (name.value.equals("get") || name.value.equals("set")) {
						// FIXME: Methods named get / set
						throw new ParserNotImplemented(position(), "Parsing class getter / setters");
					}

					final boolean isConstructor = name.value.equals("constructor");
					if (isConstructor && constructor != null) throw new SyntaxError("A class may only have one constructor", elementStart);

					if (state.is(TokenType.Equals, TokenType.Semicolon)) {
						throw new ParserNotImplemented(position(), "Parsing class fields");
					}

					final FunctionParameters parameters = parseFunctionParameters(true);
					consumeAllLineTerminators();
					final BlockStatement body = parseFunctionBody();

					if (isConstructor) {
						constructor = new ClassConstructorNode(className, parameters, body, isDerived, range(elementStart));
					} else {
						methods.add(new ClassMethodNode(className, name.value, parameters, body, range(elementStart)));
					}
				}

				consumeAllSeparators();
			} else if (state.is(TokenType.Hashtag)) {
				throw new ParserNotImplemented(position(), "Parsing private class fields");
			} else {
				state.unexpected();
			}
		}

		state.require(TokenType.RBrace);
		return new ClassExpression(className, heritage, constructor, methods, fields, range(start));
	}

	// FIXME: ClassDeclaration
	private VariableDeclaration parseClassDeclaration() throws SyntaxError, CannotParse {
		final ClassExpression classExpression = parseClassExpression();
		return new VariableDeclaration(VariableDeclaration.Kind.Let, new VariableDeclarator(new IdentifierExpression(classExpression.className()), classExpression, null));
	}

	private Expression parseClassHeritage() throws SyntaxError, CannotParse {
		if (!state.optional(TokenType.Extends)) return null;
		consumeAllLineTerminators();
		if (!state.token.matchPrimaryExpression()) state.unexpected();
		return parseExpression();
	}

	private TemplateLiteral parseTemplateLiteral() throws SyntaxError, CannotParse {
		state.require(TokenType.TemplateStart);
		final TemplateLiteral result = new TemplateLiteral();

		while (true) {
			if (state.is(TokenType.TemplateSpan)) {
				result.spanNode(state.consume().value);
			} else if (state.optional(TokenType.TemplateExpressionStart)) {
				final Expression expression = parseExpression();
				state.require(TokenType.TemplateExpressionEnd);
				result.expressionNode(expression);
			} else if (state.is(TokenType.TemplateEnd)) {
				break;
			} else {
				state.unexpected();
			}
		}

		state.require(TokenType.TemplateEnd);
		return result;
	}

	private ArrowFunctionExpression tryParseArrowFunctionExpression() throws CannotParse, SyntaxError {
		save();

		final FunctionParameters parameters;
		try {
			parameters = parseFunctionParameters(false);
		} catch (SyntaxError | CannotParse e) {
			load();
			return null;
		}

		if (!state.optional(TokenType.Arrow)) {
			load();
			return null;
		}

		// At this point, we know it's an arrow function
		consumeAllLineTerminators();
		return parseArrowFunctionBody(parameters);
	}

	private ArrowFunctionExpression parseArrowFunctionBody(FunctionParameters parameters) throws CannotParse, SyntaxError {
		if (state.is(TokenType.LBrace)) {
			return new ArrowFunctionExpression(parseBlockStatement(), parameters);
		} else if (state.token.matchPrimaryExpression()) {
			return new ArrowFunctionExpression(parseSpecAssignmentExpression(), parameters);
		} else {
			state.unexpected();
			return null;
		}
	}

	private ArrayExpression parseArrayExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(TokenType.LBracket);
		final ExpressionList elements = parseExpressionList(false, true);
		state.require(TokenType.RBracket);
		return new ArrayExpression(range(start), elements);
	}
}