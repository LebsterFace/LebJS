package xyz.lebster.parser;

import xyz.lebster.core.expression.*;
import xyz.lebster.core.node.*;
import xyz.lebster.core.value.*;
import xyz.lebster.exception.CannotParse;
import xyz.lebster.exception.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.lebster.parser.Associativity.Left;
import static xyz.lebster.parser.Associativity.Right;

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
		precedence.put(TokenType.Star, 15);
		precedence.put(TokenType.Slash, 15);
		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);
		precedence.put(TokenType.StrictEqual, 11);
		precedence.put(TokenType.StrictNotEqual, 11);
		precedence.put(TokenType.Equals, 3);

		associativity.put(TokenType.Star, Left);
		associativity.put(TokenType.Slash, Left);
		associativity.put(TokenType.Plus, Left);
		associativity.put(TokenType.Minus, Left);
		associativity.put(TokenType.Period, Left);
		associativity.put(TokenType.StrictEqual, Left);
		associativity.put(TokenType.StrictNotEqual, Left);
		associativity.put(TokenType.Equals, Right);
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
				program.append(parseLine());
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
				case If -> parseIfStatement();
				case Semicolon -> new EmptyStatement();
				case LBrace -> parseBlockStatement();

				case Return -> {
					consume();
//					FIXME: Proper automatic semicolon insertion
					final Expression val = matchExpression() ? parseExpression(0, Left) : new Undefined();
					yield new ReturnStatement(val);
				}

				default -> throw new CannotParse(currentToken.type, "Statement");
			};
		}
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
			default -> throw new CannotParse(currentToken.type, "Declaration");
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
//		TODO: Generalize this with parseFunctionDeclaration()
		require(TokenType.Function);
//		FIXME: Get name properly
		final Token idToken = accept(TokenType.Identifier);
		final Identifier name = idToken == null ? null : new Identifier(idToken.value);
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

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws ParseException {
		Expression latestExpr = parsePrimaryExpression();

		while (matchSecondaryExpression()) {
			final int newPrecedence = precedence.get(currentToken.type);

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left) {
				break;
			}

			final Associativity newAssoc = associativity.get(currentToken.type);
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
		}

		return latestExpr;
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws ParseException {
		switch (currentToken.type) {
			case Plus: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Add);
			}

			case Minus: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Subtract);
			}

			case Star: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Multiply);
			}

			case Slash: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Divide);
			}

			case Period: {
				consume();
				final String prop = require(TokenType.Identifier).value;
				return new MemberExpression(left, new Identifier(prop), false);
			}

			case LBracket: {
				consume();
				final Expression prop = parseExpression(0, Left);
				require(TokenType.RBracket);
				return new MemberExpression(left, prop, true);
			}

			case LParen: {
				consume();
				return parseCallExpression(left);
			}

			case Equals: {
				consume();
				return new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentOp.Equals);
			}

			case StrictEqual: {
				consume();
				return new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityOp.StrictEquals);
			}

			case StrictNotEqual: {
				consume();
				return new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityOp.StrictNotEquals);
			}

			default:
				return left;
		}
	}

	private Expression parsePrimaryExpression() throws ParseException {
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

			default -> throw new CannotParse("Expression type '" + currentToken.type + "'");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type;
		return t == TokenType.Function	||
			   t == TokenType.Let		||
			   t == TokenType.Var		||
			   t == TokenType.Const;
	}

	private boolean matchStatementOrExpression() {
		final TokenType t = currentToken.type;
		return matchExpression()		||
			   t == TokenType.Return	||
			   t == TokenType.Yield		||
			   t == TokenType.Do		||
			   t == TokenType.If		||
			   t == TokenType.Throw		||
			   t == TokenType.Try		||
			   t == TokenType.While		||
			   t == TokenType.With		||
			   t == TokenType.For		||
			   t == TokenType.LBrace	||
			   t == TokenType.Switch	||
			   t == TokenType.Break		||
			   t == TokenType.Continue	||
			   t == TokenType.Var		||
			   t == TokenType.Debugger	||
			   t == TokenType.Semicolon;

	}

	private boolean matchExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.StringLiteral	 ||
			   t == TokenType.NumericLiteral ||
			   t == TokenType.BooleanLiteral ||
			   t == TokenType.Null			 ||
			   t == TokenType.Function		 ||
			   t == TokenType.Infinity		 ||
			   t == TokenType.Undefined		 ||
			   t == TokenType.NaN			 ||
			   t == TokenType.This			 ||
			   t == TokenType.Identifier	 ||
			   t == TokenType.LParen;
	}

	private boolean matchSecondaryExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.Plus 			 ||
			   t == TokenType.Minus			 ||
			   t == TokenType.Star			 ||
			   t == TokenType.Slash			 ||
			   t == TokenType.StrictEqual	 ||
			   t == TokenType.StrictNotEqual ||
			   t == TokenType.Period		 ||
			   t == TokenType.LBracket		 ||
			   t == TokenType.LParen		 ||
			   t == TokenType.Equals;
	}
}
