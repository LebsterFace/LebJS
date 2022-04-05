package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.AppendableNode;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.declaration.Declaration;
import xyz.lebster.core.node.declaration.FunctionDeclaration;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.declaration.VariableDeclarator;
import xyz.lebster.core.node.expression.*;
import xyz.lebster.core.node.expression.literal.BooleanLiteral;
import xyz.lebster.core.node.expression.literal.NullLiteral;
import xyz.lebster.core.node.expression.literal.NumericLiteral;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.statement.*;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

import java.util.*;

import static xyz.lebster.core.parser.Associativity.Left;
import static xyz.lebster.core.parser.Associativity.Right;

public final class Parser {
	private ParserState state;
	private ParserState saved = null;
	private boolean hasConsumedSeparator = false;

	public Parser(Token[] tokens) {
		this.state = new ParserState(tokens);
	}

	@SpecificationURL("https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table")
	private int precedenceForTokenType(TokenType type) {
		return switch (type) {
			case Period, LBracket, LParen, OptionalChain -> 20;
			case New -> 19;
			case PlusPlus, MinusMinus -> 18;
			case Bang, Tilde, Typeof, Void, Delete, Await -> 17;
			case Exponent -> 16;
			case Star, Slash, Percent -> 15;
			case Plus, Minus -> 14;
			case LeftShift, RightShift, UnsignedRightShift -> 13;
			case LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, Instanceof -> 12;
			case LooseEqual, NotEqual, StrictEqual, StrictNotEqual -> 11;
			case Ampersand -> 10;
			case Caret -> 9;
			case Pipe -> 8;
			case NullishCoalescing -> 7;
			case LogicalAnd -> 6;
			case LogicalOr -> 5;
			case QuestionMark -> 4;
			case Equals, PlusEquals, MinusEquals, ExponentEquals, NullishCoalescingEquals, LogicalOrEquals,
				LogicalAndEquals, PipeEquals, CaretEquals, AmpersandEquals, UnsignedRightShiftEquals, RightShiftEquals,
				LeftShiftEquals, PercentEquals, DivideEquals, MultiplyEquals -> 3;
			case Yield -> 2;
			case Comma -> 1;
			default -> throw new ShouldNotHappen("Attempting to get precedence for token type '" + state.currentToken.type + "'");
		};
	}

	@SpecificationURL("https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table")
	private Associativity associativityForTokenType(TokenType type) {
		return switch (type) {
			case Period, LBracket, LParen, OptionalChain, Star, Slash, Percent, Plus, Minus, LeftShift, RightShift,
				UnsignedRightShift, LessThan, LessThanEqual, GreaterThan, GreaterThanEqual, In, Instanceof, LooseEqual,
				NotEqual, StrictEqual, StrictNotEqual, Typeof, Void, Delete, Await, Ampersand, Caret, Pipe,
				NullishCoalescing, LogicalAnd, LogicalOr, Comma -> Associativity.Left;

			case New, PlusPlus, MinusMinus, Bang, Tilde, Exponent, QuestionMark, Equals, PlusEquals, MinusEquals,
				ExponentEquals, NullishCoalescingEquals, LogicalOrEquals, LogicalAndEquals, PipeEquals, CaretEquals,
				AmpersandEquals, UnsignedRightShiftEquals, RightShiftEquals, LeftShiftEquals, PercentEquals,
				DivideEquals, MultiplyEquals, Yield -> Associativity.Right;

			default -> throw new ShouldNotHappen("Attempting to get associativity for token type '" + state.currentToken.type + "'");
		};
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
		state.consume();
		populateAppendableNode(program, TokenType.EOF);
		return program;
	}

	private <T extends AppendableNode> void populateAppendableNode(T root, TokenType end) throws CannotParse, SyntaxError {
		boolean isFirstStatement = true;
		while (state.index < state.tokens.length && state.currentToken.type != end) {
			if (isFirstStatement) {
				isFirstStatement = false;
				consumeAllSeparators();
			} else if (didConsumeSeparator()) {
				consumeAllSeparators();
			} else {
				requireAtLeastOneSeparator();
			}

			if (state.currentToken.type == end) break;
			root.append(parseAny());
		}
	}

	private void requireAtLeastOneSeparator() throws SyntaxError {
		boolean done = false;
		while (state.currentToken.type != TokenType.EOF) {
			if (state.currentToken.type == TokenType.LineTerminator ||
				state.currentToken.type == TokenType.Semicolon) {
				done = true;
				state.consume();
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
		while (state.currentToken.type != TokenType.EOF &&
			   (state.currentToken.type == TokenType.LineTerminator ||
				state.currentToken.type == TokenType.Semicolon)) {

			hasConsumedSeparator = true;
			state.consume();
		}
	}

	private void consumeAllLineTerminators() {
		while (state.currentToken.type != TokenType.EOF &&
			   state.currentToken.type == TokenType.LineTerminator) {

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
		if (matchDeclaration()) {
			return parseDeclaration();
		} else if (matchStatementOrExpression()) {
			return parseStatementOrExpression();
		} else {
			throw new CannotParse("Token '" + state.currentToken.type + "'");
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
		return switch (state.currentToken.type) {
			case Function -> parseFunctionDeclaration();
			case Semicolon -> new EmptyStatement();
			case LBrace -> parseBlockStatement();
			case While -> parseWhileStatement();
			case Do -> parseDoWhileStatement();
			case For -> parseForStatement();
			case If -> parseIfStatement();
			case Try -> parseTryStatement();
			case Switch -> parseSwitchStatement();

			case Return -> {
				state.consume();
				// FIXME: Proper automatic semicolon insertion
				yield new ReturnStatement(matchPrimaryExpression() ? parseExpression() : null);
			}

			case Break -> parseBreakStatement();
			case Continue -> parseContinueStatement();

			case Import, Export -> throw new NotImplemented("Parsing import / export statements");

			case Throw -> {
				state.consume();
				yield new ThrowStatement(parseExpression());
			}

			default -> {
				if (matchPrimaryExpression()) {
					yield new ExpressionStatement(parseExpression());
				} else {
					throw new CannotParse(state.currentToken, "Statement");
				}
			}
		};
	}

	private Statement parseSwitchStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Switch);
		state.require(TokenType.LParen);
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
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
		while (
			state.currentToken.type != TokenType.RBrace &&
			state.currentToken.type != TokenType.EOF
		) {
			consumeAllLineTerminators();
			if (state.currentToken.type == TokenType.Default) {
				state.consume();
				state.require(TokenType.Colon);
				cases.add(new SwitchCase(null, parseSwitchCaseStatements()));
			} else if (state.currentToken.type == TokenType.Case) {
				state.consume();
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
		if (!state.inContinueContext)
			throw new SyntaxError("Illegal `continue` statement");
		state.consume();
		return new ContinueStatement();
	}

	private BreakStatement parseBreakStatement() throws SyntaxError {
		if (!state.inBreakContext)
			throw new SyntaxError("Illegal `break` statement");
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

	private ForOfStatement parseForOfStatement(VariableDeclaration declaration) throws SyntaxError, CannotParse {
		// for ( LetOrConst ForBinding of AssignmentExpression ) Statement
		if (declaration.declarations().length != 1)
			throw new SyntaxError("Invalid left-hand side in for-of loop: Must have a single binding.");
		final VariableDeclarator declarator = declaration.declarations()[0];
		if (declarator.init() != null) throw new SyntaxError("for-of loop variable declaration may not have an init.");
		final BindingPattern bindingPattern = new BindingPattern(declaration.kind(), declarator.identifier());

		state.require(TokenType.Identifier, "of");
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForOfStatement(bindingPattern, expression, body);
	}

	private ForOfStatement parseForOfStatement(Expression left_expression) throws SyntaxError, CannotParse {
		// for ( LeftHandSideExpression of AssignmentExpression ) Statement
		final LeftHandSideExpression lhs = ensureLHS(left_expression, "Invalid left-hand side in for-loop");
		state.require(TokenType.Identifier, "of");
		final Expression expression = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseContextualStatement(true, true);
		return new ForOfStatement(lhs, expression, body);
	}

	private Statement parseForStatement() throws SyntaxError, CannotParse {
		// TODO: `for .. in`
		state.require(TokenType.For);
		state.require(TokenType.LParen);

		Statement init = null;
		if (state.currentToken.type != TokenType.Semicolon) {
			if (matchVariableDeclaration()) {
				// TODO: for_loop_variable_declaration
				final var declaration = parseVariableDeclaration(/* true */);
				if (state.match(TokenType.Identifier, "of")) return parseForOfStatement(declaration);
				else init = declaration;
			} else if (matchPrimaryExpression()) {
				final var expression = parseExpression();
				if (state.match(TokenType.Identifier, "of")) return parseForOfStatement(expression);
				else init = new ExpressionStatement(expression);
			} else {
				state.unexpected();
			}
		}
		state.require(TokenType.Semicolon);

		final Expression test = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.Semicolon);

		final Expression update = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.RParen);

		final Statement body = parseContextualStatement(true, true);
		return new ForStatement(init, test, update, body);
	}

	private WhileStatement parseWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.While);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseContextualStatement(true, true);
		return new WhileStatement(condition, body);
	}

	private DoWhileStatement parseDoWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Do);
		final Statement body = parseContextualStatement(true, true);
		state.require(TokenType.While);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		return new DoWhileStatement(body, condition);
	}

	private TryStatement parseTryStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Try);
		final BlockStatement body = parseBlockStatement();
		state.require(TokenType.Catch);
		state.require(TokenType.LParen);
		final String parameter = state.require(TokenType.Identifier);
		state.require(TokenType.RParen);
		final BlockStatement catchBody = parseBlockStatement();
		return new TryStatement(body, new CatchClause(parameter, catchBody));
	}

	private IfStatement parseIfStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.If);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement consequence = parseLine();
		final Statement elseStatement = state.currentToken.type == TokenType.Else ? parseElseStatement() : null;
		return new IfStatement(condition, consequence, elseStatement);
	}

	public Statement parseElseStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Else);
		return parseLine();
	}

	private Declaration parseDeclaration() throws SyntaxError, CannotParse {
		return switch (state.currentToken.type) {
			case Let, Var, Const -> parseVariableDeclaration();
			case Function -> parseFunctionDeclaration();
			case Class -> throw new NotImplemented("Parsing class declarations");
			default -> throw new CannotParse(state.currentToken, "Declaration");
		};
	}

	private VariableDeclaration parseVariableDeclaration() throws SyntaxError, CannotParse {
		// TODO: Missing init in 'const' declaration
		final var kind = switch (state.currentToken.type) {
			case Var -> VariableDeclaration.Kind.Var;
			case Let -> VariableDeclaration.Kind.Let;
			case Const -> VariableDeclaration.Kind.Const;
			default -> throw new IllegalStateException("Unexpected value: " + state.currentToken.type);
		};

		state.consume();

		final List<VariableDeclarator> declarators = new ArrayList<>();
		while (true) {
			if (state.currentToken.type == TokenType.LBrace || state.currentToken.type == TokenType.LBracket)
				throw new NotImplemented("Parsing destructuring assignment");

			final String identifier = state.require(TokenType.Identifier);
			final Expression value = state.accept(TokenType.Equals) == null ? null : parseExpression(1, Left);
			declarators.add(new VariableDeclarator(identifier, value));
			consumeAllLineTerminators();
			if (state.currentToken.type != TokenType.Comma) break;
			state.consume();
			consumeAllLineTerminators();
		}

		return new VariableDeclaration(kind, declarators.toArray(new VariableDeclarator[0]));
	}

	private String[] parseStringList() {
		final List<String> result = new ArrayList<>();
		consumeAllLineTerminators();
		while (state.currentToken.type == TokenType.Identifier) {
			consumeAllLineTerminators();
			result.add(state.consume().value);
			consumeAllLineTerminators();
			if (state.accept(TokenType.Comma) == null) break;
		}

		return result.toArray(new String[0]);
	}

	private String[] parseFunctionArguments() throws SyntaxError {
		state.require(TokenType.LParen);
		final String[] arguments = parseStringList();
		this.FAIL_FOR_UNSUPPORTED_ARG();
		state.require(TokenType.RParen);
		return arguments;
	}

	private void FAIL_FOR_UNSUPPORTED_ARG() {
		if (state.currentToken.type == TokenType.DotDotDot)
			throw new NotImplemented("Parsing rest (`...`) arguments");
		else if (state.currentToken.type == TokenType.LBrace || state.currentToken.type == TokenType.LBracket)
			throw new NotImplemented("Parsing destructuring arguments");
		else if (state.currentToken.type == TokenType.Equals)
			throw new NotImplemented("Parsing default parameters");
	}

	private FunctionDeclaration parseFunctionDeclaration() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		if (state.currentToken.type != TokenType.Identifier) {
			throw new SyntaxError("Function declarations require a function name");
		}

		final String name = state.consume().value;
		final String[] arguments = parseFunctionArguments();
		return new FunctionDeclaration(parseFunctionBody(), name, arguments);
	}

	private FunctionExpression parseFunctionExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		final Token potentialName = state.accept(TokenType.Identifier);
		final String name = potentialName == null ? null : potentialName.value;
		final String[] arguments = parseFunctionArguments();
		return new FunctionExpression(parseFunctionBody(), name, arguments);
	}

	private ExpressionList parseExpressionList(boolean expectParens) throws SyntaxError, CannotParse {
		final ExpressionList result = new ExpressionList();
		if (expectParens) state.require(TokenType.LParen);
		consumeAllLineTerminators();

		while (matchPrimaryExpression() || state.currentToken.type == TokenType.DotDotDot) {
			consumeAllLineTerminators();
			if (state.currentToken.type == TokenType.DotDotDot) {
				state.consume();
				result.addSpreadExpression(parseExpression(1, Left));
			} else {
				result.addSingleExpression(parseExpression(1, Left));
			}

			consumeAllLineTerminators();
			if (state.accept(TokenType.Comma) == null) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		if (expectParens) state.require(TokenType.RParen);
		return result;
	}

	private CallExpression parseCallExpression(Expression left) throws SyntaxError, CannotParse {
		final ExpressionList arguments = parseExpressionList(false);
		state.require(TokenType.RParen);
		return new CallExpression(left, arguments);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-UnaryExpression")
	private Expression parseUnaryPrefixedExpression() throws SyntaxError, CannotParse {
		final Token token = state.consume();
		final Associativity assoc = associativityForTokenType(token.type);
		final int minPrecedence = precedenceForTokenType(token.type);

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
		final Associativity assoc = associativityForTokenType(token.type);
		final int minPrecedence = precedenceForTokenType(token.type);

		final UpdateExpression.UpdateOp op = switch (token.type) {
			case PlusPlus -> UpdateExpression.UpdateOp.PreIncrement;
			case MinusMinus -> UpdateExpression.UpdateOp.PreDecrement;
			default -> throw new CannotParse(token, "Prefix update Operator");
		};

		return new UpdateExpression(ensureLHS(parseExpression(minPrecedence, assoc), UpdateExpression.invalidPreLHS), op);
	}

	private Expression parseExpression(Set<TokenType> forbidden) throws SyntaxError, CannotParse {
		return parseExpression(0, Left, forbidden);
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

		while (matchSecondaryExpression(forbidden)) {
			final int newPrecedence = precedenceForTokenType(state.currentToken.type);

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left)
				break;

			final Associativity newAssoc = associativityForTokenType(state.currentToken.type);
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
		}

		return latestExpr;
	}

	private LeftHandSideExpression ensureLHS(Expression expression, String failureMessage) throws SyntaxError {
		if (expression instanceof final LeftHandSideExpression leftHandSideExpression) {
			return leftHandSideExpression;
		} else {
			throw new SyntaxError(failureMessage);
		}
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		consumeAllLineTerminators();
		final Token token = state.consume();
		consumeAllLineTerminators();

		return switch (token.type) {
			case Plus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Add);
			case Minus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Subtract);
			case Star -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Multiply);
			case Slash -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Divide);
			case Percent -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Remainder);
			case Exponent -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Exponentiate);

			case Pipe -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.BitwiseOR);
			case Ampersand -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.BitwiseAND);
			case Caret -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.BitwiseXOR);
			case LeftShift -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.LeftShift);
			case RightShift -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.SignedRightShift);
			case UnsignedRightShift -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.UnsignedRightShift);

			case QuestionMark -> parseConditionalExpression(left);
			case LParen -> parseCallExpression(left);

			case StrictEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictEquals);
			case LooseEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.LooseEquals);
			case StrictNotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictNotEquals);
			case NotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.LooseNotEquals);

			case LogicalOr -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.Or);
			case LogicalAnd -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.And);
			case NullishCoalescing -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.Coalesce);

			case PlusEquals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.PlusAssign);
			case MinusEquals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.MinusAssign);
			case MultiplyEquals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.MultiplyAssign);
			case DivideEquals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.DivideAssign);
			case ExponentEquals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.ExponentAssign);
			case Equals -> new AssignmentExpression(ensureLHS(left, AssignmentExpression.invalidLHS), parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.Assign);

			case MinusMinus -> new UpdateExpression(ensureLHS(left, UpdateExpression.invalidPostLHS), UpdateExpression.UpdateOp.PostDecrement);
			case PlusPlus -> new UpdateExpression(ensureLHS(left, UpdateExpression.invalidPostLHS), UpdateExpression.UpdateOp.PostIncrement);

			case LessThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.LessThan);
			case LessThanEqual -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.LessThanEquals);
			case GreaterThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.GreaterThan);
			case GreaterThanEqual -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.GreaterThanEquals);
			case In -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.In);
			case Instanceof -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.InstanceOf);

			case Period -> {
				if (!matchIdentifierName()) state.expected("IdentifierName");
				yield new MemberExpression(left, new StringLiteral(new StringValue(state.consume().value)), false);
			}

			case LBracket -> {
				final Expression prop = parseExpression();
				state.require(TokenType.RBracket);
				yield new MemberExpression(left, prop, true);
			}

			case Comma -> {
				final Expression next = parseExpression();
				yield new SequenceExpression(left, next);
			}

			default -> throw new CannotParse(token, "SecondaryExpression");
		};
	}

	private Expression parseConditionalExpression(Expression test) throws CannotParse, SyntaxError {
		final Expression left = parseExpression(2, Right);
		state.require(TokenType.Colon);
		final Expression right = parseExpression(2, Right);
		return new ConditionalExpression(test, left, right);
	}

	private Expression parsePrimaryExpression() throws SyntaxError, CannotParse {
		if (matchPrefixedUpdateExpression()) {
			return parsePrefixedUpdateExpression();
		} else if (matchUnaryPrefixedExpression()) {
			return parseUnaryPrefixedExpression();
		}

		return switch (state.currentToken.type) {
			case LParen -> {
				state.consume();
				if (state.currentToken.type == TokenType.RParen || state.currentToken.type == TokenType.Identifier) {
					final ArrowFunctionExpression result = tryParseArrowFunctionExpression();
					if (result != null) yield result;
				}

				consumeAllLineTerminators();
				final Expression expression = parseExpression();
				consumeAllLineTerminators();
				state.require(TokenType.RParen);
				yield expression;
			}

			case Identifier -> {
				final String identifier = state.consume().value;
				if (state.currentToken.type == TokenType.Arrow) {
					state.consume();
					final ArrowFunctionExpression arrowFunction = parseArrowFunctionBody(identifier);
					if (arrowFunction == null) state.unexpected();
					yield arrowFunction;
				} else {
					yield new IdentifierExpression(identifier);
				}
			}

			case StringLiteral -> new StringLiteral(new StringValue(state.consume().value));
			case NumericLiteral -> new NumericLiteral(new NumberValue(Double.parseDouble(state.consume().value)));

			case True -> {
				state.consume();
				yield new BooleanLiteral(BooleanValue.TRUE);
			}

			case False -> {
				state.consume();
				yield new BooleanLiteral(BooleanValue.FALSE);
			}

			case Await -> throw new NotImplemented("Parsing `await` expressions");

			case Function -> parseFunctionExpression();
			case LBracket -> parseArrayExpression();
			case LBrace -> parseObjectExpression();

			case This -> {
				state.consume();
				yield new ThisKeyword();
			}

			case Null -> {
				state.consume();
				yield new NullLiteral();
			}

			case New -> {
				final TokenType n = state.consume().type;
				// https://tc39.es/ecma262/multipage#sec-new-operator
				final Expression constructExpr = parseExpression(precedenceForTokenType(n), associativityForTokenType(n), Collections.singleton(TokenType.LParen));
				final boolean hasArguments = state.currentToken.type == TokenType.LParen;
				final ExpressionList arguments = hasArguments ? parseExpressionList(true) : new ExpressionList(); // TODO: Perhaps `null`?
				yield new NewExpression(constructExpr, arguments);
			}

			default -> throw new CannotParse(state.currentToken, "PrimaryExpression");
		};
	}

	private ArrowFunctionExpression tryParseArrowFunctionExpression() throws SyntaxError, CannotParse {
		save();

		final String[] arguments = parseStringList();
		this.FAIL_FOR_UNSUPPORTED_ARG();
		if (state.currentToken.type != TokenType.RParen) {
			load();
			return null;
		}

		state.consume();
		if (state.accept(TokenType.Arrow) == null) {
			load();
			return null;
		}

		final ArrowFunctionExpression result = parseArrowFunctionBody(arguments);
		if (result == null) {
			load();
			return null;
		} else {
			return result;
		}
	}

	private ArrowFunctionExpression parseArrowFunctionBody(String... arguments) throws CannotParse, SyntaxError {
		if (state.currentToken.type == TokenType.LBrace) {
			return new ArrowFunctionExpression(parseBlockStatement(), arguments);
		} else if (matchPrimaryExpression()) {
			// NOTE: Minimum precedence of 1 (excludes TokenType.Comma): `() => 1,2` is not valid
			return new ArrowFunctionExpression(parseExpression(1, Left), arguments);
		} else {
			return null;
		}
	}

	private ObjectExpression parseObjectExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBrace);
		consumeAllLineTerminators();
		final ObjectExpression result = new ObjectExpression();

		// FIXME:
		//      - Spread { ...object }
		// 		- Methods { a() { alert(1) } }
		// 		- Getters / Setters { get a() { return Math.random() } }
		while (state.currentToken.type != TokenType.RBrace) {
			Expression key;
			String propertyName = null;
			boolean isIdentifier = matchIdentifierName();

			if (isIdentifier || state.is(TokenType.NumericLiteral, TokenType.StringLiteral)) {
				propertyName = state.consume().value;
				key = new StringLiteral(new StringValue(propertyName));
			} else {
				state.require(TokenType.LBracket);
				key = parseExpression();
				state.require(TokenType.RBracket);
			}

			consumeAllLineTerminators();
			if (isIdentifier && state.currentToken.type != TokenType.Colon) {
				result.entries().put(key, new IdentifierExpression(propertyName));
			} else {
				state.require(TokenType.Colon);
				consumeAllLineTerminators();
				result.entries().put(key, parseExpression(1, Left));
			}

			consumeAllLineTerminators();
			if (state.accept(TokenType.Comma) == null) break;
			consumeAllLineTerminators();
		}

		consumeAllLineTerminators();
		state.require(TokenType.RBrace);
		return result;
	}

	private ArrayExpression parseArrayExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBracket);
		final ExpressionList elements = parseExpressionList(false);
		state.require(TokenType.RBracket);
		return new ArrayExpression(elements);
	}

	private boolean matchIdentifierName() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Let ||
			   t == TokenType.Identifier ||
			   t == TokenType.Await ||
			   t == TokenType.Break ||
			   t == TokenType.Case ||
			   t == TokenType.Catch ||
			   t == TokenType.Class ||
			   t == TokenType.Const ||
			   t == TokenType.Continue ||
			   t == TokenType.Debugger ||
			   t == TokenType.Default ||
			   t == TokenType.Delete ||
			   t == TokenType.Do ||
			   t == TokenType.Else ||
			   t == TokenType.Enum ||
			   t == TokenType.Export ||
			   t == TokenType.Extends ||
			   t == TokenType.False ||
			   t == TokenType.Finally ||
			   t == TokenType.For ||
			   t == TokenType.Function ||
			   t == TokenType.If ||
			   t == TokenType.Import ||
			   t == TokenType.In ||
			   t == TokenType.Instanceof ||
			   t == TokenType.New ||
			   t == TokenType.Null ||
			   t == TokenType.Return ||
			   t == TokenType.Super ||
			   t == TokenType.Switch ||
			   t == TokenType.This ||
			   t == TokenType.Throw ||
			   t == TokenType.True ||
			   t == TokenType.Try ||
			   t == TokenType.Typeof ||
			   t == TokenType.Var ||
			   t == TokenType.Void ||
			   t == TokenType.While ||
			   t == TokenType.With ||
			   t == TokenType.Yield;
	}

	private boolean matchDeclaration() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Function ||
			   t == TokenType.Class ||
			   t == TokenType.Let ||
			   t == TokenType.Var ||
			   t == TokenType.Const;
	}

	private boolean matchStatementOrExpression() {
		final TokenType t = state.currentToken.type;
		return matchPrimaryExpression() ||
			   t == TokenType.Return ||
			   t == TokenType.Yield ||
			   t == TokenType.Do ||
			   t == TokenType.If ||
			   t == TokenType.Throw ||
			   t == TokenType.Try ||
			   t == TokenType.While ||
			   t == TokenType.With ||
			   t == TokenType.For ||
			   t == TokenType.LBrace ||
			   t == TokenType.Switch ||
			   t == TokenType.Break ||
			   t == TokenType.Continue ||
			   t == TokenType.Var ||
			   t == TokenType.Import ||
			   t == TokenType.Export ||
			   t == TokenType.Debugger ||
			   t == TokenType.Semicolon;
	}

	private boolean matchPrimaryExpression() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.LParen ||
			   t == TokenType.Identifier ||
			   t == TokenType.StringLiteral ||
			   t == TokenType.NumericLiteral ||
			   t == TokenType.True ||
			   t == TokenType.False ||
			   t == TokenType.Function ||
			   t == TokenType.LBracket ||
			   t == TokenType.LBrace ||
			   t == TokenType.This ||
			   t == TokenType.Null ||
			   t == TokenType.New ||
			   t == TokenType.Infinity ||
			   t == TokenType.NaN ||
			   t == TokenType.Undefined ||
			   matchUnaryPrefixedExpression() ||
			   matchPrefixedUpdateExpression();
	}

	private boolean matchSecondaryExpression(Set<TokenType> forbidden) {
		final TokenType t = state.currentToken.type;
		if (forbidden.contains(t)) return false;
		return t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Star ||
			   t == TokenType.Slash ||
			   t == TokenType.Percent ||
			   t == TokenType.LeftShift ||
			   t == TokenType.RightShift ||
			   t == TokenType.UnsignedRightShift ||
			   t == TokenType.Caret ||
			   t == TokenType.QuestionMark ||
			   t == TokenType.Comma ||
			   t == TokenType.Exponent ||
			   t == TokenType.StrictEqual ||
			   t == TokenType.StrictNotEqual ||
			   t == TokenType.LooseEqual ||
			   t == TokenType.NotEqual ||
			   t == TokenType.LogicalOr ||
			   t == TokenType.LogicalAnd ||
			   t == TokenType.Period ||
			   t == TokenType.LessThan ||
			   t == TokenType.LessThanEqual ||
			   t == TokenType.GreaterThan ||
			   t == TokenType.GreaterThanEqual ||
			   t == TokenType.PlusEquals ||
			   t == TokenType.MinusEquals ||
			   t == TokenType.DivideEquals ||
			   t == TokenType.MultiplyEquals ||
			   t == TokenType.LeftShiftEquals ||
			   t == TokenType.RightShiftEquals ||
			   t == TokenType.NullishCoalescing ||
			   t == TokenType.In ||
			   t == TokenType.Pipe ||
			   t == TokenType.Ampersand ||
			   t == TokenType.Instanceof ||
			   t == TokenType.LBracket ||
			   t == TokenType.LParen ||
			   t == TokenType.Equals ||
			   t == TokenType.PlusPlus ||
			   t == TokenType.MinusMinus;
	}

	private boolean matchPrefixedUpdateExpression() {
		return state.currentToken.type == TokenType.PlusPlus ||
			   state.currentToken.type == TokenType.MinusMinus;
	}

	private boolean matchUnaryPrefixedExpression() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Bang ||
			   t == TokenType.Tilde ||
			   t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Typeof ||
			   t == TokenType.Void ||
			   t == TokenType.Delete;
	}

	private boolean matchVariableDeclaration() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Var ||
			   t == TokenType.Let ||
			   t == TokenType.Const;
	}
}