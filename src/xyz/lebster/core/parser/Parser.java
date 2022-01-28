package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
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
		this.saved = state.clone();
	}

	private void load() {
		if (this.saved == null) throw new IllegalStateException("Attempting to load invalid ParseState");
		this.state = saved;
		this.saved = null;
	}

	public Program parse() throws SyntaxError, CannotParse {
		final Program program = new Program();
		state.consume();

		while (state.index < state.tokens.length) {
			if (state.currentToken.type == TokenType.EOF) {
				break;
			} else {
				program.append(parseLine());
			}
		}

		return program;
	}

	private Statement parseLine() throws SyntaxError, CannotParse {
		state.consumeAll(TokenType.Terminator);
		final Statement result = parseAny();
		state.consumeAll(TokenType.Semicolon);
		state.consumeAll(TokenType.Terminator);
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

		while (state.index < state.tokens.length && state.currentToken.type != TokenType.RBrace) {
			if (state.currentToken.type == TokenType.EOF) {
				break;
			} else {
				result.append(parseLine());
			}
		}

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

			case Return -> {
				state.consume();
				// FIXME: Proper automatic semicolon insertion
				yield new ReturnStatement(matchPrimaryExpression() ? parseExpression() : null);
			}

			case Break -> {
				state.consume();
				yield new BreakStatement();
			}

			case Import, Export -> throw new NotImplemented("Parsing import / export statements");
			case Continue -> throw new NotImplemented("Parsing continue statements");
			case Switch -> throw new NotImplemented("Parsing switch statements");

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

	private ForStatement parseForStatement() throws SyntaxError, CannotParse {
		// FIXME: For .. of & For .. in
		state.require(TokenType.For);
		state.require(TokenType.LParen);

		Statement init = null;
		if (matchPrimaryExpression() || matchDeclaration()) init = parseAny();
		state.require(TokenType.Semicolon);

		final Expression test = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.Semicolon);

		final Expression update = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.RParen);

		final Statement body = parseLine();
		return new ForStatement(init, test, update, body);
	}

	private WhileStatement parseWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.While);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseLine();
		return new WhileStatement(condition, body);
	}

	private DoWhileStatement parseDoWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Do);
		final Statement body = parseLine();
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
		final String parameter = state.require(TokenType.Identifier).value;
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

			final Token identifier = state.require(TokenType.Identifier);
			final Expression value = state.accept(TokenType.Equals) == null ? null : parseExpression();
			declarators.add(new VariableDeclarator(identifier.value, value));
			state.consumeAll(TokenType.Terminator);
			if (state.currentToken.type != TokenType.Comma) break;
			state.consume();
			state.consumeAll(TokenType.Terminator);
		}

		return new VariableDeclaration(kind, declarators.toArray(new VariableDeclarator[0]));
	}

	private String[] parseStringList() {
		final List<String> result = new ArrayList<>();
		while (state.currentToken.type == TokenType.Identifier) {
			result.add(state.consume().value);
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

	private String[] parseArrowFunctionArguments(boolean expectParens) {
		if (!expectParens) {
			final Token t = state.accept(TokenType.Identifier);
			if (t == null) return null;
			return new String[] { t.value };
		}


		final String[] result = parseStringList();
		this.FAIL_FOR_UNSUPPORTED_ARG();
		if (state.accept(TokenType.RParen) == null) return null;
		return result;
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
		final String name = state.require(TokenType.Identifier).value;
		final String[] arguments = parseFunctionArguments();
		return new FunctionDeclaration(parseBlockStatement(), name, arguments);
	}

	private FunctionExpression parseFunctionExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		final Token potentialName = state.accept(TokenType.Identifier);
		final String name = potentialName == null ? null : potentialName.value;
		final String[] arguments = parseFunctionArguments();
		return new FunctionExpression(parseBlockStatement(), name, arguments);
	}

	private List<Expression> parseExpressionList(boolean expectParens) throws SyntaxError, CannotParse {
		final List<Expression> result = new ArrayList<>();
		if (expectParens) state.require(TokenType.LParen);
		state.consumeAll(TokenType.Terminator);

		while (matchPrimaryExpression()) {
			state.consumeAll(TokenType.Terminator);
			result.add(parseExpression());
			state.consumeAll(TokenType.Terminator);
			if (state.accept(TokenType.Comma) == null) break;
			state.consumeAll(TokenType.Terminator);
		}

		state.consumeAll(TokenType.Terminator);
		if (expectParens) state.require(TokenType.RParen);
		return result;
	}

	private CallExpression parseCallExpression(Expression left) throws SyntaxError, CannotParse {
		final List<Expression> arguments = parseExpressionList(false);
		if (state.currentToken.type == TokenType.DotDotDot)
			throw new NotImplemented("Parsing spread arguments (`fn(...p)`)");
		state.require(TokenType.RParen);
		return new CallExpression(left, arguments.toArray(new Expression[0]));
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

	private Expression parseExpression() throws SyntaxError, CannotParse {
		return parseExpression(0, Left, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		return parseExpression(minPrecedence, assoc, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc, Set<TokenType> forbidden) throws SyntaxError, CannotParse {
		Expression latestExpr = parsePrimaryExpression();

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
		state.consumeAll(TokenType.Terminator);
		final Token token = state.consume();
		state.consumeAll(TokenType.Terminator);

		return switch (token.type) {
			case Plus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Add);
			case Minus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Subtract);
			case Star -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Multiply);
			case Slash -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Divide);
			case Percent -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Remainder);
			case Exponent -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Exponent);

			case Pipe, Ampersand -> throw new NotImplemented("Parsing binary bitwise expressions");
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

			default -> throw new CannotParse(state.currentToken, "SecondaryExpression");
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
					final FunctionExpression result = tryParseArrowFunctionExpression(true);
					if (result != null) yield result;
				}

				state.consumeAll(TokenType.Terminator);
				final Expression expression = parseExpression();
				state.consumeAll(TokenType.Terminator);
				state.require(TokenType.RParen);
				yield expression;
			}

			case Identifier -> {
				final FunctionExpression result = tryParseArrowFunctionExpression(false);
				yield result != null ? result : new IdentifierExpression(state.consume().value);
			}

			case StringLiteral -> new StringLiteral(new StringValue(state.consume().value));
			case NumericLiteral -> new NumericLiteral(new NumberValue(Double.parseDouble(state.consume().value)));
			case BooleanLiteral -> new BooleanLiteral(BooleanValue.of(state.consume().value.equals("true")));
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
				final List<Expression> arguments = state.currentToken.type == TokenType.LParen ? parseExpressionList(true) : Collections.emptyList();
				yield new NewExpression(constructExpr, arguments.toArray(new Expression[0]));
			}

			default -> throw new CannotParse(state.currentToken, "PrimaryExpression");
		};
	}

	private FunctionExpression tryParseArrowFunctionExpression(boolean expectParens) throws SyntaxError, CannotParse {
		save();

		final String[] arguments = parseArrowFunctionArguments(expectParens);
		if (arguments == null) {
			load();
			return null;
		}

		if (state.accept(TokenType.Arrow) == null) {
			load();
			return null;
		}

		final BlockStatement body = parseArrowFunctionBody();
		if (body == null) {
			load();
			return null;
		}

		return new FunctionExpression(body, null, arguments);
	}

	private BlockStatement parseArrowFunctionBody() throws CannotParse, SyntaxError {
		if (state.currentToken.type == TokenType.LBrace) {
			return parseBlockStatement();
		} else if (matchPrimaryExpression()) {
			final BlockStatement body = new BlockStatement();
			body.append(new ReturnStatement(parseExpression()));
			return body;
		} else {
			return null;
		}
	}

	private ObjectExpression parseObjectExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBrace);
		state.consumeAll(TokenType.Terminator);
		final ObjectExpression result = new ObjectExpression();

		// FIXME:
		// 		- Methods { a() { alert(1) } }
		// 		- Getters / Setters { get a() { return Math.random() } }
		// 		- Computed property names { ["a" + "b"]: 123 }
		// 		- Shorthand initializers { a, b }
		// 		- Allow all property names { 0: "hello" }
		while (state.currentToken.type == TokenType.StringLiteral || state.currentToken.type == TokenType.Identifier) {
			final var key = new StringLiteral(new StringValue(state.consume().value));
			state.consumeAll(TokenType.Terminator);
			state.require(TokenType.Colon);
			state.consumeAll(TokenType.Terminator);
			result.entries().put(key, parseExpression());
			state.consumeAll(TokenType.Terminator);
			if (state.accept(TokenType.Comma) == null) break;
			state.consumeAll(TokenType.Terminator);
		}

		state.consumeAll(TokenType.Terminator);
		state.require(TokenType.RBrace);
		return result;
	}

	private ArrayExpression parseArrayExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBracket);
		final List<Expression> elements = parseExpressionList(false);
		if (state.currentToken.type == TokenType.DotDotDot)
			throw new NotImplemented("Parsing spread syntax `[...a]`");
		state.require(TokenType.RBracket);
		return new ArrayExpression(elements);
	}

	private boolean matchIdentifierName() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Let ||
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
			   t == TokenType.Export ||
			   t == TokenType.Extends ||
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
			   t == TokenType.Try ||
			   t == TokenType.Typeof ||
			   t == TokenType.Var ||
			   t == TokenType.Void ||
			   t == TokenType.While ||
			   t == TokenType.With ||
			   t == TokenType.Yield ||
			   t == TokenType.Identifier;
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
			   t == TokenType.BooleanLiteral ||
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
			   t == TokenType.QuestionMark ||
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
}