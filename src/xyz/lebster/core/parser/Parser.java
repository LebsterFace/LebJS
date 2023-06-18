package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.ParserNotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.*;
import xyz.lebster.core.node.declaration.*;
import xyz.lebster.core.node.expression.*;
import xyz.lebster.core.node.expression.ClassExpression.ClassConstructorNode;
import xyz.lebster.core.node.expression.ClassExpression.ClassFieldNode;
import xyz.lebster.core.node.expression.ClassExpression.ClassMethodNode;
import xyz.lebster.core.node.expression.ObjectExpression.*;
import xyz.lebster.core.node.expression.UpdateExpression.UpdateOp;
import xyz.lebster.core.node.expression.literal.NumericLiteral;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.expression.literal.*;
import xyz.lebster.core.node.statement.*;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.*;

import static xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp;
import static xyz.lebster.core.parser.Associativity.Left;
import static xyz.lebster.core.parser.Associativity.Right;
import static xyz.lebster.core.parser.TokenType.Class;
import static xyz.lebster.core.parser.TokenType.*;

public final class Parser {
	private final String sourceText;
	private final ArrayDeque<ParserState> savedStack = new ArrayDeque<>();
	private ParserState state;
	private boolean hasConsumedSeparator = false;

	public Parser(String sourceText, Token[] tokens) {
		this.sourceText = sourceText;
		this.state = new ParserState(tokens);
	}

	public Parser(String sourceText) throws SyntaxError {
		this(sourceText, Lexer.tokenize(sourceText));
	}

	private void save() {
		this.savedStack.add(state.copy());
	}

	private void load() {
		if (this.savedStack.isEmpty()) throw new IllegalStateException("Attempting to load invalid ParseState");
		this.state = savedStack.removeLast();
	}

	public Program parse() throws SyntaxError, CannotParse {
		final Program program = new Program();
		populateAppendableNode(program, EOF);
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
		while (!state.is(EOF)) {
			if (state.optional(LineTerminator, Semicolon)) {
				done = true;
			} else {
				if (done) {
					return;
				} else {
					throw state.unexpected();
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
		while (!state.is(EOF) && state.is(LineTerminator, Semicolon)) {
			hasConsumedSeparator = true;
			state.consume();
		}
	}

	private void consumeAllLineTerminators() {
		while (!state.is(EOF) && state.is(LineTerminator)) {
			hasConsumedSeparator = true;
			state.consume();
		}
	}

	private Statement parseLine() throws SyntaxError, CannotParse {
		consumeAllLineTerminators();
		final Statement result = parseStatementOrExpression();
		consumeAllSeparators();
		return result;
	}

	private Statement parseAny() throws SyntaxError, CannotParse {
		if (state.token.matchDeclaration()) {
			return parseDeclaration();
		} else if (state.token.matchPrimaryExpression() || state.token.matchStatement()) {
			return parseStatementOrExpression();
		} else {
			throw state.unexpected();
		}
	}

	private BlockStatement parseBlockStatement() throws SyntaxError, CannotParse {
		state.require(LBrace);
		final BlockStatement result = new BlockStatement();
		populateAppendableNode(result, RBrace);
		state.require(RBrace);
		return result;
	}

	private Statement parseStatementOrExpression() throws SyntaxError, CannotParse {
		return switch (state.token.type) {
			case Import, Export -> throw new ParserNotImplemented(position(), "import / export statements");

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
					final Expression expression = parseExpression();
					if (state.is(Colon))
						throw new ParserNotImplemented(position(), "labels");
					yield new ExpressionStatement(expression);
				} else {
					throw new CannotParse(state.token, "Statement");
				}
			}
		};
	}

	private Statement parseSwitchStatement() throws SyntaxError, CannotParse {
		state.require(Switch);
		consumeAllLineTerminators();
		state.require(LParen);
		consumeAllLineTerminators();
		final Expression expression = parseExpression();
		state.require(RParen);
		consumeAllLineTerminators();
		state.require(LBrace);
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
		while (!(state.is(RBrace, EOF))) {
			consumeAllLineTerminators();
			if (state.optional(Default)) {
				state.require(Colon);
				cases.add(new SwitchCase(null, parseSwitchCaseStatements()));
			} else if (state.optional(Case)) {
				final Expression test = parseExpression();
				state.require(Colon);
				cases.add(new SwitchCase(test, parseSwitchCaseStatements()));
			} else {
				throw state.expected(Case);
			}

			consumeAllSeparators();
		}

		state.require(RBrace);
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

			if (state.is(Case, Default, RBrace, EOF))
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
		state.requireIdentifier("of");
		final Expression expression = parseExpression();
		state.require(RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForOfStatement(left, expression, body);
	}

	private ForInStatement parseForInStatement(Assignable left) throws SyntaxError, CannotParse {
		state.require(In);
		final Expression expression = parseExpression();
		state.require(RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForInStatement(left, expression, body);
	}

	private Statement parseForStatement() throws SyntaxError, CannotParse {
		state.require(For);
		consumeAllLineTerminators();
		state.require(LParen);
		consumeAllLineTerminators();

		Statement init = null;
		if (!state.is(Semicolon)) {
			if (state.token.matchVariableDeclaration()) {
				// TODO: for_loop_variable_declaration
				final VariableDeclaration declaration = parseVariableDeclaration(/* true */);
				if (state.is(Identifier, "of")) {
					// for ( LetOrConst ForBinding of AssignmentExpression ) Statement
					if (declaration.declarations().length != 1) throw new SyntaxError("Invalid left-hand side in for-of loop: Must have a single binding.", position());
					if (declaration.declarations()[0].init() != null) throw new SyntaxError("for-of loop variable declaration may not have an init.", position());

					return parseForOfStatement(new ForBinding(declaration));
				} else if (state.is(In)) {
					// for ( LetOrConst ForBinding in Expression ) Statement
					if (declaration.declarations().length != 1) throw new SyntaxError("Invalid left-hand side in for-in loop: Must have a single binding.", position());
					if (declaration.declarations()[0].init() != null) throw new SyntaxError("for-in loop variable declaration may not have an init.", position());

					return parseForInStatement(new ForBinding(declaration));
				} else {
					init = declaration;
				}
			} else if (state.token.matchPrimaryExpression()) {
				final Expression expression = parseExpression(0, Associativity.Right, Collections.singleton(In));
				if (state.is(Identifier, "of")) {
					return parseForOfStatement(ensureLHS(expression, "Invalid left-hand side in for-loop"));
				} else if (state.is(In)) {
					return parseForInStatement(ensureLHS(expression, "Invalid left-hand side in for-loop"));
				} else {
					init = new ExpressionStatement(expression);
				}
			} else {
				throw state.unexpected();
			}
		}

		state.require(Semicolon);
		consumeAllLineTerminators();

		final Expression test = state.token.matchPrimaryExpression() ? parseExpression() : null;
		state.require(Semicolon);
		consumeAllLineTerminators();

		final Expression update = state.token.matchPrimaryExpression() ? parseExpression() : null;
		state.require(RParen);

		final Statement body = parseContextualStatement(true, true);
		return new ForStatement(init, test, update, body);
	}

	private Expression parseWhileCondition() throws SyntaxError, CannotParse {
		state.require(While);
		consumeAllLineTerminators();
		state.require(LParen);
		consumeAllLineTerminators();
		final Expression condition = parseExpression();
		state.require(RParen);
		return condition;
	}

	private WhileStatement parseWhileStatement() throws SyntaxError, CannotParse {
		final Expression condition = parseWhileCondition();
		final Statement body = parseContextualStatement(true, true);
		return new WhileStatement(condition, body);
	}

	private DoWhileStatement parseDoWhileStatement() throws SyntaxError, CannotParse {
		state.require(Do);
		final Statement body = parseContextualStatement(true, true);
		final Expression condition = parseWhileCondition();
		return new DoWhileStatement(body, condition);
	}

	private TryStatement parseTryStatement() throws SyntaxError, CannotParse {
		state.require(Try);
		final BlockStatement body = parseBlockStatement();
		consumeAllLineTerminators();

		StringValue catchParameter = null;
		BlockStatement catchBody = null;
		final boolean hasCatch = state.optional(Catch);
		if (hasCatch) {
			if (state.optional(LParen)) {
				catchParameter = new StringValue(state.require(Identifier));
				state.require(RParen);
			}

			catchBody = parseBlockStatement();
			consumeAllLineTerminators();
		}

		BlockStatement finallyBody = null;
		if (state.optional(Finally)) {
			finallyBody = parseBlockStatement();
		} else if (!hasCatch) {
			throw new SyntaxError("Missing catch or finally after try", position());
		}

		return new TryStatement(body, catchParameter, catchBody, finallyBody);
	}

	private IfStatement parseIfStatement() throws SyntaxError, CannotParse {
		state.require(If);
		consumeAllLineTerminators();
		state.require(LParen);
		consumeAllLineTerminators();
		final Expression condition = parseExpression();
		state.require(RParen);
		final Statement consequence = parseLine();
		final Statement elseStatement = state.optional(Else) ? parseLine() : null;
		return new IfStatement(condition, consequence, elseStatement);
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
			case Var -> Kind.Var;
			case Let -> Kind.Let;
			case Const -> Kind.Const;
			default -> throw state.unexpected();
		};

		state.consume();

		final List<VariableDeclarator> declarators = new ArrayList<>();
		while (true) {
			declarators.add(parseVariableDeclarator());
			consumeAllLineTerminators();
			if (!state.is(Comma)) break;
			state.consume();
			consumeAllLineTerminators();
		}

		return new VariableDeclaration(kind, declarators.toArray(new VariableDeclarator[0]));
	}

	private VariableDeclarator parseVariableDeclarator() throws SyntaxError, CannotParse {
		final SourcePosition declaratorStart = position();
		final AssignmentTarget lhs = parseAssignmentTarget(false);
		final Expression value = state.optional(Equals) ? parseSpecAssignmentExpression() : null;
		return new VariableDeclarator(lhs, value, range(declaratorStart));
	}

	private AssignmentPattern parseInitializer(final AssignmentTarget assignmentTarget) throws CannotParse, SyntaxError {
		// =(opt) Expression(opt)
		consumeAllLineTerminators();
		if (!state.optional(Equals)) // No default expression
			return new AssignmentPattern(assignmentTarget, null);

		consumeAllLineTerminators();
		final Expression defaultExpression = parseSpecAssignmentExpression();
		return new AssignmentPattern(assignmentTarget, defaultExpression);
	}

	private AssignmentTarget parseAssignmentTarget(boolean allowMemberExpressions) throws SyntaxError, CannotParse {
		if (state.optional(LBrace)) {
			return parseObjectDestructuring(allowMemberExpressions);
		} else if (state.optional(LBracket)) {
			return parseArrayDestructuring(allowMemberExpressions);
		}

		final Expression expression = parseExpression(2, Associativity.Right, Set.of(Equals, In));
		if (expression instanceof final AssignmentTarget assignmentTarget) {
			if (allowMemberExpressions || expression instanceof IdentifierExpression) return assignmentTarget;
			throw new SyntaxError("Illegal property in declaration context", position());
		} else {
			throw new SyntaxError("Invalid destructuring assignment target", position());
		}
	}

	private ArrayDestructuring parseArrayDestructuring(boolean allowMemberExpressions) throws SyntaxError, CannotParse {
		final ArrayList<AssignmentPattern> children = new ArrayList<>();
		AssignmentTarget restTarget = null;

		consumeAllLineTerminators();
		while (true) {
			consumeAllLineTerminators();
			if (state.optional(Comma)) {
				children.add(null);
			} else if (state.optional(DotDotDot)) {
				consumeAllLineTerminators();
				restTarget = parseAssignmentTarget(allowMemberExpressions);
				consumeAllLineTerminators();
				if (state.optional(Comma)) throw new SyntaxError("Rest element must be last element", position());
				break;
			} else {
				children.add(parseInitializer(parseAssignmentTarget(allowMemberExpressions)));
				consumeAllLineTerminators();
				if (!state.optional(Comma)) break;
			}
		}

		state.require(RBracket);
		return new ArrayDestructuring(restTarget, children.toArray(new AssignmentPattern[0]));
	}

	private ObjectDestructuring parseObjectDestructuring(boolean allowMemberExpressions) throws SyntaxError, CannotParse {
		consumeAllLineTerminators();

		final Map<Expression, AssignmentPattern> pairs = new HashMap<>();
		StringValue restName = null;
		while (!state.is(RBrace)) {
			if (state.optional(DotDotDot)) {
				consumeAllLineTerminators();
				if (state.is(Identifier)) {
					restName = new StringValue(state.consume().value);
					consumeAllLineTerminators();
					if (!state.optional(Comma)) break;
					consumeAllLineTerminators();
					continue;
				} else {
					throw new SyntaxError("`...` must be followed by an identifier in declaration contexts", position());
				}
			}

			final Token keyToken = state.token;
			if (keyToken.matchIdentifierName() || keyToken.type == NumericLiteral || keyToken.type == StringLiteral) {
				final StringLiteral key = state.consume().asStringLiteral();
				consumeAllLineTerminators();

				if (state.optional(Colon)) {
					consumeAllLineTerminators();
					pairs.put(key, parseInitializer(parseAssignmentTarget(allowMemberExpressions)));
				} else {
					if (keyToken.type != Identifier) throw state.unexpected(keyToken);
					pairs.put(key, parseInitializer(new IdentifierExpression(key.value())));
				}
			} else {
				pairs.put(parseComputedKeyExpression(), parseInitializer(parseAssignmentTarget(allowMemberExpressions)));
			}

			consumeAllLineTerminators();
			if (!state.optional(Comma)) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		state.require(RBrace);
		return new ObjectDestructuring(pairs, restName);
	}

	private Expression parseComputedKeyExpression() throws SyntaxError, CannotParse {
		state.require(LBracket);
		final Expression keyExpression = parseExpression();
		state.require(RBracket);
		consumeAllLineTerminators();
		state.require(Colon);
		consumeAllLineTerminators();
		return keyExpression;
	}

	private FunctionParameters parseFunctionParameters(boolean expectLParen) throws SyntaxError, CannotParse {
		if (expectLParen) state.require(LParen);

		final FunctionParameters result = new FunctionParameters();
		consumeAllLineTerminators();
		while (!state.is(RParen)) {
			consumeAllLineTerminators();
			if (state.optional(DotDotDot)) {
				// Note: Rest parameter may not have a default initializer
				consumeAllLineTerminators();
				result.rest = parseAssignmentTarget(false);
				consumeAllLineTerminators();
				if (state.optional(Comma)) throw new SyntaxError("Rest parameter must be last formal parameter", position());
				break;
			} else {
				final AssignmentTarget target = parseAssignmentTarget(false);
				consumeAllLineTerminators();
				if (state.optional(Equals)) {
					final Expression defaultExpression = parseSpecAssignmentExpression();
					result.addWithDefault(target, defaultExpression);
				} else {
					result.add(target);
				}
			}

			consumeAllLineTerminators();
			if (!state.optional(Comma)) break;
		}

		state.require(RParen);
		return result;
	}

	private FunctionDeclaration parseFunctionDeclaration() throws SyntaxError, CannotParse {
		state.require(Function);
		if (state.is(Star))
			throw new ParserNotImplemented(position(), "Generator function declarations");
		consumeAllLineTerminators();
		if (!state.is(Identifier))
			throw new SyntaxError("Function declarations require a function name", position());

		final StringLiteral name = state.consume().asStringLiteral();
		consumeAllLineTerminators();
		final FunctionParameters parameters = parseFunctionParameters(true);
		consumeAllLineTerminators();
		return new FunctionDeclaration(parseFunctionBody(), name, parameters);
	}

	private FunctionExpression parseFunctionExpression(boolean allowName) throws SyntaxError, CannotParse {
		if (state.is(Star)) throw new ParserNotImplemented(position(), "Generator function expressions");
		final StringLiteral name = allowName ? state.optionalStringLiteral(Identifier) : null;
		final FunctionParameters parameters = parseFunctionParameters(true);
		return new FunctionExpression(parseFunctionBody(), name, parameters);
	}

	private ExpressionList parseExpressionList(boolean expectParens, boolean canHaveEmpty) throws SyntaxError, CannotParse {
		final ExpressionList result = new ExpressionList(canHaveEmpty);
		if (expectParens) state.require(LParen);
		consumeAllLineTerminators();

		while (state.token.matchPrimaryExpression() || state.is(DotDotDot, Comma)) {
			consumeAllLineTerminators();
			if (state.optional(DotDotDot)) {
				result.addSpreadExpression(parseSpecAssignmentExpression());
			} else if (canHaveEmpty && state.optional(Comma)) {
				result.addEmpty();
				consumeAllLineTerminators();
				continue;
			} else {
				result.addSingleExpression(parseSpecAssignmentExpression());
			}

			consumeAllLineTerminators();
			if (!state.optional(Comma)) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		if (expectParens) state.require(RParen);
		return result;
	}

	private CallExpression parseCallExpression(Expression left) throws SyntaxError, CannotParse {
		final ExpressionList arguments = parseExpressionList(false, false);
		state.require(RParen);
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
			final var newAssoc = state.token.associativity();
			if (newPrecedence < minPrecedence) break;
			if (newPrecedence == minPrecedence && assoc == Left) break;

			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
			checkForInvalidProperty(latestExpr);
			consumeAllLineTerminators();
		}

		checkForInvalidProperty(latestExpr);
		return latestExpr;
	}

	private void checkForInvalidProperty(Expression latestExpr) throws SyntaxError {
		if (latestExpr instanceof final ObjectExpression objectExpression) {
			final SourcePosition position = state.invalidProperties.get(objectExpression);
			if (position != null) {
				throw new SyntaxError("Invalid shorthand property initializer", position);
			}
		}
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
			case Plus, Minus, Star, Slash, Percent, Exponent, Pipe, Ampersand, Caret, LeftShift, RightShift, UnsignedRightShift -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), token.getBinaryOp());
			case StrictEqual, LooseEqual, StrictNotEqual, NotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), token.getEqualityOp());
			case LogicalOr, LogicalAnd, NullishCoalescing -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), token.getLogicOp());
			case LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, InstanceOf -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), token.getRelationalOp());
			case MinusMinus, PlusPlus -> new UpdateExpression(ensureLHS(left, UpdateExpression.invalidPostLHS), token.getUpdateOp());
			case Equals, LogicalAndEquals, LogicalOrEquals, NullishCoalescingEquals, MultiplyEquals,
				DivideEquals, PercentEquals, PlusEquals, MinusEquals, LeftShiftEquals, RightShiftEquals,
				UnsignedRightShiftEquals, AmpersandEquals, CaretEquals, PipeEquals, ExponentEquals -> new AssignmentExpression(ensureAssignable(left, token.getAssignmentOp()), parseExpression(minPrecedence, assoc), token.getAssignmentOp());

			case QuestionMark -> parseConditionalExpression(left);
			case LParen -> parseCallExpression(left);
			case Period -> parseNonComputedMemberExpression(left);
			case LBracket -> parseComputedMemberExpression(left);
			case Comma -> new SequenceExpression(left, parseExpression());

			case OptionalChain -> throw new ParserNotImplemented(position(), "optional chaining");
			default -> throw new CannotParse(token, "SecondaryExpression");
		};
	}

	private MemberExpression parseComputedMemberExpression(Expression left) throws SyntaxError, CannotParse {
		final Expression prop = parseExpression();
		state.require(RBracket);
		return new MemberExpression(left, prop, true);
	}

	private MemberExpression parseNonComputedMemberExpression(Expression left) throws SyntaxError {
		if (!state.token.matchIdentifierName()) throw state.expected("IdentifierName");
		return new MemberExpression(left, state.consume().asStringLiteral(), false);
	}

	private Assignable ensureAssignable(Expression left_expr, AssignmentOp op) throws SyntaxError, CannotParse {
		if (left_expr instanceof final Assignable assignable) {
			if (left_expr instanceof LeftHandSideExpression || op == AssignmentOp.Assign) {
				return assignable;
			}
		} else if (op == AssignmentOp.Assign && (left_expr instanceof ArrayExpression || left_expr instanceof ObjectExpression)) {
			// left_expr is a destructuring pattern we mis-parsed as an array / object literal
			// TODO: Convert to DestructuringAssignmentTarget manually, rather than re-parsing the source
			return new Parser(left_expr.range().getText()).parseAssignmentTarget(true);
		}

		throw new SyntaxError(AssignmentExpression.invalidLHS, position());
	}

	private Expression parseConditionalExpression(Expression test) throws CannotParse, SyntaxError {
		final Expression left = parseExpression(2, Right);
		consumeAllLineTerminators();
		state.require(Colon);
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
			case Await -> throw new ParserNotImplemented(position(), "`await` expressions");
			case Async -> throw new ParserNotImplemented(position(), "`async` functions");
			case BigIntLiteral -> throw new ParserNotImplemented(position(), "BigIntLiterals");
			case Super -> throw new ParserNotImplemented(position(), "Super property access");

			case Class -> parseClassExpression();
			case LBracket -> parseArrayExpression();
			case LBrace -> parseObjectExpression();
			case TemplateStart -> parseTemplateLiteral();
			case New -> parseNewExpression();
			case LParen -> parseParenthesizedExpressionOrArrowFunctionExpression();
			case Identifier -> parseIdentifierExpressionOrArrowFunctionExpression();
			case RegexpPattern -> parseRegexpLiteral();
			case StringLiteral -> state.consume().asStringLiteral();
			case NumericLiteral -> parseNumericLiteral(false);

			case Function -> {
				state.require(Function);
				yield parseFunctionExpression(true);
			}

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
		final String value = (leadingDecimal ? "." : "") + state.require(NumericLiteral);
		return new NumericLiteral(new NumberValue(Double.parseDouble(value)));
	}

	private RegExpLiteral parseRegexpLiteral() throws SyntaxError {
		final String pattern = state.require(RegexpPattern);
		final String flags = state.is(RegexpFlags) ? state.consume().value : "";
		return new RegExpLiteral(pattern, flags);
	}

	private Expression parseIdentifierExpressionOrArrowFunctionExpression() throws CannotParse, SyntaxError {
		final var identifier = new IdentifierExpression(state.consume().value);
		if (state.is(TemplateStart)) throw new ParserNotImplemented(position(), "tagged template literals");
		if (!state.optional(Arrow)) return identifier;
		return parseArrowFunctionBody(new FunctionParameters(identifier));
	}

	private Expression parseParenthesizedExpressionOrArrowFunctionExpression() throws CannotParse, SyntaxError {
		final SourcePosition start = state.consume().position;

		consumeAllLineTerminators();
		if (state.is(RParen, Identifier, DotDotDot, LBrace, LBracket)) {
			final ArrowFunctionExpression result = tryParseArrowFunctionExpression();
			if (result != null) return result;
		}

		consumeAllLineTerminators();
		final Expression expression = parseExpression();
		consumeAllLineTerminators();
		state.require(RParen);
		return new ParenthesizedExpression(expression, range(start));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-new-operator")
	private NewExpression parseNewExpression() throws SyntaxError, CannotParse {
		final Token newOperator = state.consume();
		consumeAllLineTerminators();
		if (state.is(Period)) throw new ParserNotImplemented(position(), "new.target");
		final Expression constructExpr = parseExpression(newOperator.precedence(), newOperator.associativity(), Collections.singleton(LParen));
		final boolean hasArguments = state.is(LParen);
		final ExpressionList arguments = hasArguments ? parseExpressionList(true, false) : null;
		return new NewExpression(constructExpr, arguments);
	}

	// FIXME: Correctly handle super in all cases
	private SuperCallStatement parseSuperCall() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(Super);
		final ExpressionList args = parseExpressionList(true, false);
		return new SuperCallStatement(args, range(start));
	}

	private ClassExpression parseClassExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(Class);
		consumeAllLineTerminators();
		final Token potentialName = state.accept(Identifier);
		final String className = potentialName == null ? null : potentialName.value;
		consumeAllLineTerminators();
		final Expression heritage = parseClassHeritage();
		consumeAllLineTerminators();
		return parseClassBody(start, className, heritage);
	}

	private ObjectExpressionKey parseObjectExpressionKey() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		final boolean isIdentifier = state.token.matchIdentifierName();
		final boolean nonComputed = isIdentifier || state.is(NumericLiteral, StringLiteral);
		final Expression key;
		if (nonComputed) {
			key = state.consume().asStringLiteral();
		} else {
			state.require(LBracket);
			key = parseExpression();
			state.require(RBracket);
		}

		consumeAllLineTerminators();
		return new ObjectExpressionKey(start, key, !nonComputed, isIdentifier);
	}

	private ObjectExpression parseObjectExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(LBrace);
		consumeAllLineTerminators();
		final ObjectExpression result = new ObjectExpression();

		while (!state.is(RBrace)) {
			final var property = parseObjectProperty(result);
			if (property != null) result.entries.add(property);
			consumeAllLineTerminators();
			if (!state.optional(Comma)) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		state.require(RBrace);
		result.range = range(start);
		return result;
	}

	private ObjectEntryNode parseObjectProperty(ObjectExpression result) throws CannotParse, SyntaxError {
		if (state.optional(DotDotDot)) {
			consumeAllLineTerminators();
			return new SpreadNode(parseSpecAssignmentExpression());
		} else if (state.optional(Star))
			throw new ParserNotImplemented(position(), "generator object literal methods");

		final ObjectExpressionKey key = parseObjectExpressionKey();
		consumeAllLineTerminators();

		if (!key.computed) {
			final String prefix = key.stringValue().value;
			if (state.token.matchObjectExpressionKey())
				if (prefix.equals("async")) {
					throw new ParserNotImplemented(position(), "`async` object literal methods");
				} else if (prefix.equals("get") || prefix.equals("set")) {
					consumeAllLineTerminators();
					final ObjectExpressionKey name = parseObjectExpressionKey();
					final FunctionExpression function = parseFunctionExpression(false);
					return new GetterSetterNode(prefix.equals("get"), name.expression, function, name.computed);
				}
		}

		if (key.isIdentifier && state.optional(Equals)) {
			// Not a valid object literal, but a valid destructuring pattern
			// Parse the expression and throw it away
			parseSpecAssignmentExpression();
			if (!state.invalidProperties.containsKey(result)) {
				state.invalidProperties.put(result, key.start);
			}

			return null;
		}

		if (state.is(LParen)) {
			final FunctionExpression function = parseFunctionExpression(false);
			return new EntryNode(key.expression, function, key.computed, true);
		}

		if (state.optional(Colon)) {
			consumeAllLineTerminators();
			final Expression value = parseSpecAssignmentExpression();
			return new EntryNode(key.expression, value, key.computed, false);
		}

		return new ShorthandNode(key.stringValue());
	}

	private ClassExpression parseClassBody(SourcePosition start, String className, Expression heritage) throws SyntaxError, CannotParse {
		ClassConstructorNode constructor = null;
		final List<ClassMethodNode> methods = new ArrayList<>();
		final List<ClassFieldNode> fields = new ArrayList<>();
		state.require(LBrace);
		consumeAllSeparators();

		final boolean isDerived = heritage != null;

		while (!state.is(RBrace)) {
			consumeAllSeparators();
			if (state.optional(Static))
				throw new ParserNotImplemented(position(), "`static` class elements");
			if (state.optional(Star))
				throw new ParserNotImplemented(position(), "generator class methods");

			if (!state.token.matchClassElementName()) {
				if (state.is(Hashtag))
					throw new ParserNotImplemented(position(), "private class fields");

				throw state.unexpected();
			}

			final SourcePosition elementStart = position();
			if (state.optional(Async))
				throw new ParserNotImplemented(position(), "`async` class methods");

			// FIXME: Methods named get / set
			if (state.optional(Identifier, "get") || state.optional(Identifier, "set"))
				throw new ParserNotImplemented(position(), "class getters & setters");

			// TODO: Remove duplication with parseObjectExpression
			final Expression name;
			final boolean computedName = !state.token.matchIdentifierName() && !state.is(NumericLiteral, StringLiteral);
			final boolean isConstructor = state.is(Identifier, "constructor");
			if (computedName) {
				state.require(LBracket);
				consumeAllLineTerminators();
				name = parseExpression();
				state.require(RBracket);
			} else {
				name = state.consume().asStringLiteral();
			}

			consumeAllLineTerminators();

			// FieldDefinition
			if (state.optional(Equals)) {
				consumeAllLineTerminators();
				final Expression initializer = parseSpecAssignmentExpression();
				consumeAllLineTerminators();
				fields.add(new ClassFieldNode(name, initializer, range(elementStart)));

				consumeAllSeparators();
				continue;
			}

			if (isConstructor && constructor != null)
				throw new SyntaxError("A class may only have one constructor", elementStart);

			final FunctionParameters parameters = parseFunctionParameters(true);
			consumeAllLineTerminators();
			final BlockStatement body = parseFunctionBody();

			if (isConstructor) {
				constructor = new ClassConstructorNode(className, parameters, body, isDerived, range(elementStart));
			} else {
				methods.add(new ClassMethodNode(name, computedName, parameters, body, range(elementStart)));
			}

			consumeAllSeparators();
		}

		state.require(RBrace);
		return new ClassExpression(className, heritage, constructor, methods, fields, range(start));
	}

	// FIXME: ClassDeclaration
	private VariableDeclaration parseClassDeclaration() throws SyntaxError, CannotParse {
		final ClassExpression classExpression = parseClassExpression();
		return new VariableDeclaration(Kind.Let, new VariableDeclarator(new IdentifierExpression(classExpression.className()), classExpression, null));
	}

	private Expression parseClassHeritage() throws SyntaxError, CannotParse {
		if (!state.optional(Extends)) return null;
		consumeAllLineTerminators();
		if (!state.token.matchPrimaryExpression()) throw state.unexpected();
		return parseExpression();
	}

	private TemplateLiteral parseTemplateLiteral() throws SyntaxError, CannotParse {
		state.require(TemplateStart);
		final TemplateLiteral result = new TemplateLiteral();

		while (true) {
			if (state.is(TemplateSpan)) {
				result.spanNode(state.consume().value);
			} else if (state.optional(TemplateExpressionStart)) {
				consumeAllLineTerminators();
				final Expression expression = parseExpression();
				consumeAllLineTerminators();
				state.require(TemplateExpressionEnd);
				result.expressionNode(expression);
			} else if (state.is(TemplateEnd)) {
				break;
			} else {
				throw state.unexpected();
			}
		}

		state.require(TemplateEnd);
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

		if (!state.optional(Arrow)) {
			load();
			return null;
		}

		// At this point, we know it's an arrow function
		return parseArrowFunctionBody(parameters);
	}

	private ArrowFunctionExpression parseArrowFunctionBody(FunctionParameters parameters) throws CannotParse, SyntaxError {
		consumeAllLineTerminators();
		if (state.is(LBrace)) {
			return new ArrowFunctionExpression(parseBlockStatement(), parameters);
		} else if (state.token.matchPrimaryExpression()) {
			return new ArrowFunctionExpression(parseSpecAssignmentExpression(), parameters);
		} else {
			throw state.unexpected();
		}
	}

	private ArrayExpression parseArrayExpression() throws SyntaxError, CannotParse {
		final SourcePosition start = position();
		state.require(LBracket);
		final ExpressionList elements = parseExpressionList(false, true);
		state.require(RBracket);
		return new ArrayExpression(range(start), elements);
	}

	private record ObjectExpressionKey(SourcePosition start, Expression expression, boolean computed, boolean isIdentifier) {
		private StringValue stringValue() {
			if (computed) throw new ShouldNotHappen("Cannot get non-computed key of computed key");
			return ((StringLiteral) expression).value();
		}
	}
}