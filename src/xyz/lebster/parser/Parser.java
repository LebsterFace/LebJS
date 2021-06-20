package xyz.lebster.parser;

import xyz.lebster.exception.CannotParse;
import xyz.lebster.exception.ParseError;
import xyz.lebster.core.node.*;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.StringLiteral;

import java.util.ArrayList;
import java.util.HashMap;

import static xyz.lebster.parser.Associativity.Left;
import static xyz.lebster.parser.Associativity.Right;

public class Parser {
	private static final HashMap<TokenType, Integer> precedence = new HashMap<>();
	private static final HashMap<TokenType, Associativity> associativity = new HashMap<>();

	static {
//		https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table
		precedence.put(TokenType.Multiply, 15);
		precedence.put(TokenType.Divide, 15);
		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);
		precedence.put(TokenType.Period, 20);
		precedence.put(TokenType.LParen, 21);
		precedence.put(TokenType.RParen, 21);
		precedence.put(TokenType.Equals, 3);

		associativity.put(TokenType.Multiply, Left);
		associativity.put(TokenType.Divide, Left);
		associativity.put(TokenType.Plus, Left);
		associativity.put(TokenType.Minus, Left);
		associativity.put(TokenType.Period, Left);
		associativity.put(TokenType.Equals, Right);
	}

	public final Token[] tokens;
	private Token currentToken;
	private int index = -1;

	public Parser(Token[] tokens) {
		this.tokens = tokens;
	}

	private void expected(TokenType t) throws ParseError {
		throw new ParseError("Unexpected token " + currentToken.type + ". Expected " + t);
	}

	private Token consume() {
		final Token oldToken = currentToken;
		if (index + 1 != tokens.length) index++;
		currentToken = tokens[index];
		return oldToken;
	}

	private boolean match(TokenType t) {
		return currentToken.type == t;
	}

	private Token require(TokenType t) throws ParseError {
		if (!match(t)) expected(t);
		return consume();
	}

	private Token accept(TokenType t) {
		return match(t) ? consume() : null;
	}

	public Program parse() throws ParseError {
		final Program program = new Program();
		consume();

		while (index < tokens.length) {
			if (currentToken.type == TokenType.EOF) {
				break;
			} else if (currentToken.type == TokenType.Terminator || currentToken.type == TokenType.Semicolon) {
				consume();
			} else if (matchDeclaration()) {
				program.append(parseDeclaration());
			} else if (matchExpression()) {
				program.append(parseExpression(0, Left));
			} else {
				System.out.println("------- PARTIAL TREE -------");
				program.dump(0);
				System.out.println("------- ERROR -------");
				throw new CannotParse("Token '" + currentToken.type + "'");
			}
		}

		return program;
	}

	private CallExpression parseCallExpression(Expression left) throws ParseError {
		final ArrayList<Expression> arguments = new ArrayList<>();
		while (matchExpression()) {
			arguments.add(parseExpression(0, Left));
		}

		require(TokenType.RParen);
		return new CallExpression(left, arguments.toArray(new Expression[0]));
	}

	private VariableDeclaration parseDeclaration() throws ParseError {
		require(TokenType.Let);
		final Token identifier = require(TokenType.Identifier);
		require(TokenType.Equals);
		final Expression value = parseExpression(0, Left);
		return new VariableDeclaration(
			new VariableDeclarator(new Identifier(identifier.value), value)
		);
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws ParseError {
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

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws ParseError {
		switch (currentToken.type) {
			case Plus: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Add);
			}

			case Minus: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Subtract);
			}

			case Multiply: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Multiply);
			}

			case Divide: {
				consume();
				return new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryOp.Divide);
			}

			case Period: {
				consume();
				final String prop = require(TokenType.Identifier).value;
				return new MemberExpression(left, new Identifier(prop));
			}

			case LParen: {
				consume();
				return parseCallExpression(left);
			}

			case Equals: {
				consume();
				return new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentOp.Equals);
			}

			default:
				return left;
		}
	}

	private Expression parsePrimaryExpression() throws ParseError {
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
			case This -> {
				consume();
				yield new Identifier("this");
			}

			default -> throw new CannotParse("Expression type '" + currentToken.type + "'");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type;
		return t == TokenType.Let;
	}

	private boolean matchExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.StringLiteral  ||
			   t == TokenType.NumericLiteral ||
			   t == TokenType.BooleanLiteral ||
			   t == TokenType.This			 ||
			   t == TokenType.Identifier 	 ||
			   t == TokenType.LParen;
	}

	private boolean matchSecondaryExpression() {
		final TokenType t = currentToken.type;
		return t == TokenType.Plus	   ||
			   t == TokenType.Minus	   ||
			   t == TokenType.Multiply ||
			   t == TokenType.Divide   ||
			   t == TokenType.Period   ||
			   t == TokenType.LParen   ||
			   t == TokenType.Equals;
	}
}
