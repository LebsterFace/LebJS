package xyz.lebster.parser;

import xyz.lebster.exception.CannotParse;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.exception.ParseException;
import xyz.lebster.node.*;
import xyz.lebster.node.expression.*;
import xyz.lebster.node.value.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.lebster.parser.Associativity.*;

public class Parser {
	private static final HashMap<TokenType, Integer> precedence = new HashMap<>();
	private static final HashMap<TokenType, Associativity> associativity = new HashMap<>();

	static {
//		https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table
		precedence.put(TokenType.LParen, 21);
		precedence.put(TokenType.RParen, 21);

		precedence.put(TokenType.LBracket, 20);
		precedence.put(TokenType.RBracket, 20);
		precedence.put(TokenType.Period, 20);

//		Unary Expressions:
		precedence.put(TokenType.Bang, 17);
		precedence.put(TokenType.Typeof, 17);
//		FIXME: Postfix should have higher precedence than prefix
		precedence.put(TokenType.Increment, 17);
		precedence.put(TokenType.Decrement, 17);

		precedence.put(TokenType.Star, 15);
		precedence.put(TokenType.Slash, 15);

		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);

		precedence.put(TokenType.LessThan, 12);
		precedence.put(TokenType.GreaterThan, 12);

		precedence.put(TokenType.StrictEqual, 11);
		precedence.put(TokenType.StrictNotEqual, 11);

		precedence.put(TokenType.LogicalAnd, 8);

		precedence.put(TokenType.LogicalOr, 6);

		precedence.put(TokenType.PlusEquals, 3);
		precedence.put(TokenType.MinusEquals, 3);
		precedence.put(TokenType.Equals, 3);

//		FIXME: Switch statement method (when all operators are implemented)
		associativity.put(TokenType.LParen, NA);
		associativity.put(TokenType.RParen, NA);

		associativity.put(TokenType.LBracket, Left);
		associativity.put(TokenType.RBracket, Left);
		associativity.put(TokenType.Star, Left);
		associativity.put(TokenType.Slash, Left);
		associativity.put(TokenType.Plus, Left);
		associativity.put(TokenType.Minus, Left);
		associativity.put(TokenType.Period, Left);
		associativity.put(TokenType.StrictEqual, Left);
		associativity.put(TokenType.StrictNotEqual, Left);
		associativity.put(TokenType.LessThan, Left);
		associativity.put(TokenType.GreaterThan, Left);
		associativity.put(TokenType.LogicalOr, Left);
		associativity.put(TokenType.LogicalAnd, Left);

		associativity.put(TokenType.Bang, Right);
		associativity.put(TokenType.Typeof, Right);
		associativity.put(TokenType.Increment, Right);
		associativity.put(TokenType.Decrement, Right);

		associativity.put(TokenType.Equals, Right);
		associativity.put(TokenType.PlusEquals, Right);
		associativity.put(TokenType.MinusEquals, Right);
	}

	public final Token[] tokens;
	private Token currentToken;
	private int index = -1;

	public Parser(Token[] tokens) {
		this.tokens = tokens;
	}

	private void expected(TokenType t) throws ParseException {
		throw new ParseException("Unexpected token " + currentToken.type + ". Expected " + t);
	}

	private Token consume() {
		final Token oldToken = currentToken;
		if (index + 1 != tokens.length) index++;
		currentToken = tokens[index];
		return oldToken;
	}

	private Token require(TokenType t) throws ParseException {
		if (currentToken.type != t) expected(t);
		return consume();
	}

	private Token accept(TokenType t) {
		return currentToken.type == t ? consume() : null;
	}

	private void consumeAll(TokenType t) {
		while (currentToken.type == t) consume();
	}

	public Program parse() throws ParseException {
		final Program program = new Program();
		consume();

		while (index < tokens.length) {
			if (currentToken.type == TokenType.EOF) {
				break;
			} else {
				program.body.append(parseLine());
			}
		}

		return program;
	}

	private Statement parseLine() throws ParseException {
		consumeAll(TokenType.Terminator);

		Statement result;
		if (matchDeclaration()) {
			result = parseDeclaration();
		} else if (matchStatementOrExpression()) {
			result = parseStatementOrExpression();
		} else {
			throw new CannotParse("Token '" + currentToken.type + "'");
		}

		consumeAll(TokenType.Semicolon);
		consumeAll(TokenType.Terminator);
		return result;
	}

	private BlockStatement parseBlockStatement() throws ParseException {
		require(TokenType.LBrace);
		final BlockStatement result = new BlockStatement();

		while (index < tokens.length && currentToken.type != TokenType.RBrace) {
			if (currentToken.type == TokenType.EOF) {
				break;
			} else {
				result.append(parseLine());
			}
		}

		require(TokenType.RBrace);
		return result;
	}

	private Statement parseStatementOrExpression() throws ParseException {
		if (matchExpression()) {
			return new ExpressionStatement(parseExpression(0, Left));
		} else {
			return switch (currentToken.type) {
				case Function -> parseFunctionDeclaration();
				case Semicolon -> new EmptyStatement();
				case LBrace -> parseBlockStatement();

				case Return -> {
					consume();
//					FIXME: Proper automatic semicolon insertion
					final Expression val = matchExpression() ? parseExpression(0, Left) : new Undefined();
					yield new ReturnStatement(val);
				}

				case While -> parseWhileStatement();
				case Break -> {
					consume();
					yield new BreakStatement();
				}

				case If -> parseIfStatement();
				case Try -> parseTryStatement();
				case Throw -> {
					consume();
					yield new ThrowStatement(parseExpression(0, Left));
				}

				default -> throw new CannotParse(currentToken, "Statement");
			};
		}
	}

	private WhileStatement parseWhileStatement() throws ParseException {
		require(TokenType.While);
		require(TokenType.LParen);
		final Expression condition = parseExpression(0, Left);
		require(TokenType.RParen);
		final Statement body = parseLine();
		return new WhileStatement(condition, body);
	}

	private TryStatement parseTryStatement() throws ParseException {
		require(TokenType.Try);
		final BlockStatement body = parseBlockStatement();
		require(TokenType.Catch);
		require(TokenType.LParen);
		final Identifier parameter = new Identifier(require(TokenType.Identifier).value);
		require(TokenType.RParen);
		final BlockStatement catchBody = parseBlockStatement();
		return new TryStatement(body, new CatchClause(parameter, catchBody));
	}

	private IfStatement parseIfStatement() throws ParseException {
		require(TokenType.If);
		require(TokenType.LParen);
		final Expression condition = parseExpression(0, Left);
		require(TokenType.RParen);
		final Statement consequence = parseLine();
		final Statement elseStatement = currentToken.type == TokenType.Else ? parseElseStatement() : null;
		return new IfStatement(condition, consequence, elseStatement);
	}

	public Statement parseElseStatement() throws ParseException {
		require(TokenType.Else);
		return parseLine();
	}

	private Declaration parseDeclaration() throws ParseException {
		return switch (currentToken.type) {
			case Let, Var, Const -> parseVariableDeclaration();
			case Function -> parseFunctionDeclaration();
			default -> throw new CannotParse(currentToken, "Declaration");
		};
	}

	private VariableDeclaration parseVariableDeclaration() throws ParseException {
		consume();
		final Token identifier = require(TokenType.Identifier);
		require(TokenType.Equals);
		final Expression value = parseExpression(0, Left);
		return new VariableDeclaration(new VariableDeclarator(new Identifier(identifier.value), value));
	}

	private List<Identifier> parseFunctionArguments() throws ParseException {
		require(TokenType.LParen);
		final List<Identifier> arguments = new ArrayList<>();

		while (currentToken.type == TokenType.Identifier) {
			arguments.add(new Identifier(consume().value));
			if (accept(TokenType.Comma) == null) break;
		}

		require(TokenType.RParen);
		return arguments;
	}

	private FunctionDeclaration parseFunctionDeclaration() throws ParseException {
		require(TokenType.Function);
		final Identifier name = new Identifier(require(TokenType.Identifier).value);
		final List<Identifier> arguments = parseFunctionArguments();
		return new FunctionDeclaration(parseBlockStatement(), name, arguments.toArray(new Identifier[0]));
	}

	private FunctionExpression parseFunctionExpression() throws ParseException {
		require(TokenType.Function);
		final Token potentialName = accept(TokenType.Identifier);
		final Identifier name = potentialName == null ? null : new Identifier(potentialName.value);
		final List<Identifier> arguments = parseFunctionArguments();
		return new FunctionExpression(parseBlockStatement(), name, arguments.toArray(new Identifier[0]));
	}

	private CallExpression parseCallExpression(Expression left) throws ParseException {
		final ArrayList<Expression> arguments = new ArrayList<>();
		while (matchExpression()) {
			arguments.add(parseExpression(0, Left));
			if (accept(TokenType.Comma) == null) break;
		}

		require(TokenType.RParen);
		return new CallExpression(left, arguments.toArray(new Expression[0]));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-UnaryExpression")
	private Expression parseUnaryPrefixedExpression() throws ParseException {
		final Token token = consume();
//		TODO: Remove when all implemented
		if (!associativity.containsKey(token.type))
			throw new NotImplemented("Associativity for token '" + token.type + "'");
		final Associativity assoc = associativity.get(token.type);
//		TODO: Remove when all implemented
		if (!precedence.containsKey(token.type)) throw new NotImplemented("Precedence for token '" + token.type + "'");
		final int minPrecedence = precedence.get(token.type);

		final UnaryExpression.UnaryOp op = switch (token.type) {
			case Minus -> UnaryExpression.UnaryOp.Negate;
			case Bang -> UnaryExpression.UnaryOp.LogicalNot;
			case Typeof -> UnaryExpression.UnaryOp.Typeof;
			case Increment -> UnaryExpression.UnaryOp.PreIncrement;
			case Decrement -> UnaryExpression.UnaryOp.PreDecrement;
			default -> throw new CannotParse(token, "Unary Operator");
		};

		return new UnaryExpression(parseExpression(minPrecedence, assoc), op);
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws ParseException {
		Expression latestExpr = parsePrimaryExpression();

		while (matchSecondaryExpression()) {
//			TODO: Remove when all implemented
			if (!precedence.containsKey(currentToken.type))
				throw new NotImplemented("Precedence for token '" + currentToken.type + "'");
			final int newPrecedence = precedence.get(currentToken.type);

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left) {
				break;
			}

//			TODO: Remove when all implemented
			if (!associativity.containsKey(currentToken.type))
				throw new NotImplemented("Associativity for token '" + currentToken.type + "'");
			final Associativity newAssoc = associativity.get(currentToken.type);
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
		}

		return latestExpr;
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws ParseException {
		accept(TokenType.Terminator);
		final Token token = consume();
		accept(TokenType.Terminator);

		return switch (token.type) {
			case Plus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Add);
			case Minus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Subtract);
			case Star -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Multiply);
			case Slash -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Divide);
			case LParen -> parseCallExpression(left);
			case Equals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.Assign);
			case StrictEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictEquals);
			case StrictNotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictNotEquals);
			case LogicalOr -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.Or);
			case LogicalAnd -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.And);
			case PlusEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.PlusAssign);
			case MinusEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.MinusAssign);
			case Decrement -> new UnaryExpression(left, UnaryExpression.UnaryOp.PostDecrement);
			case Increment -> new UnaryExpression(left, UnaryExpression.UnaryOp.PostIncrement);
			case LessThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.LessThan);
			case GreaterThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.GreaterThan);


			case Period -> {
				final String prop = require(TokenType.Identifier).value;
				yield new MemberExpression(left, new StringLiteral(prop), false);
			}

			case LBracket -> {
				final Expression prop = parseExpression(0, Left);
				require(TokenType.RBracket);
				yield new MemberExpression(left, prop, true);
			}

			default -> throw new CannotParse(currentToken, "SecondaryExpression");
		};
	}

	private Expression parsePrimaryExpression() throws ParseException {
		if (matchUnaryPrefixedExpression()) {
			return parseUnaryPrefixedExpression();
		}

		return switch (currentToken.type) {
			case LParen -> {
				consume();
				final Expression expression = parseExpression(0, Left);
				require(TokenType.RParen);
				yield expression;
			}

			case StringLiteral -> new StringLiteral(consume().value);
			case NumericLiteral -> new NumericLiteral(Double.parseDouble(consume().value));
			case BooleanLiteral -> new BooleanLiteral(consume().value.equals("true"));
			case Identifier -> new Identifier(consume().value);
			case Function -> parseFunctionExpression();

			case This -> {
				consume();
				yield new ThisKeyword();
			}

			case Null -> {
				consume();
				yield new Null();
			}

			case Infinity -> {
				consume();
				yield new NumericLiteral(Double.POSITIVE_INFINITY);
			}

			case NaN -> {
				consume();
				yield new NumericLiteral(Double.NaN);
			}

			case Undefined -> {
				consume();
				yield new Undefined();
			}

			default -> throw new CannotParse(currentToken, "PrimaryExpression");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type;
		return t == TokenType.Function ||
			   t == TokenType.Let ||
			   t == TokenType.Var ||
			   t == TokenType.Const;
	}

	private boolean matchStatementOrExpression() {
		final TokenType t = currentToken.type;
		return matchExpression() ||
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
			   t == TokenType.Debugger ||
			   t == TokenType.Semicolon;

	}

	private boolean matchExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.StringLiteral ||
			   t == TokenType.NumericLiteral ||
			   t == TokenType.BooleanLiteral ||
			   t == TokenType.Null ||
			   t == TokenType.Function ||
			   t == TokenType.Infinity ||
			   t == TokenType.Undefined ||
			   t == TokenType.NaN ||
			   t == TokenType.This ||
			   t == TokenType.Identifier ||
			   t == TokenType.LParen ||
			   matchUnaryPrefixedExpression();
	}

	private boolean matchSecondaryExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Star ||
			   t == TokenType.Slash ||
			   t == TokenType.StrictEqual ||
			   t == TokenType.StrictNotEqual ||
			   t == TokenType.LogicalOr ||
			   t == TokenType.LogicalAnd ||
			   t == TokenType.Period ||
			   t == TokenType.LessThan ||
			   t == TokenType.GreaterThan ||
			   t == TokenType.PlusEquals ||
			   t == TokenType.MinusEquals ||
			   t == TokenType.DivideEquals ||
			   t == TokenType.MultiplyEquals ||
			   t == TokenType.LeftShiftEquals ||
			   t == TokenType.RightShiftEquals ||
			   t == TokenType.LBracket ||
			   t == TokenType.LParen ||
			   t == TokenType.Equals ||
			   t == TokenType.Increment ||
			   t == TokenType.Decrement;
	}

	private boolean matchUnaryPrefixedExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.Increment ||
			   t == TokenType.Decrement ||
			   t == TokenType.Bang ||
			   t == TokenType.Tilde ||
			   t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Typeof ||
			   t == TokenType.Void ||
			   t == TokenType.Delete;
	}
}