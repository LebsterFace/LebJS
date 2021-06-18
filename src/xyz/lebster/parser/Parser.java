package xyz.lebster.parser;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.StringLiteral;

import java.util.HashMap;

import static xyz.lebster.parser.Associativity.*;

public class Parser {
	public final Token[] tokens;
	private Token currentToken;
	private int index = -1;

	private static final HashMap<TokenType, Integer> precedence = new HashMap<>();
	private static final HashMap<TokenType, Associativity> associativity = new HashMap<>();

	static {
//		https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table
		precedence.put(TokenType.Multiply, 15);
		precedence.put(TokenType.Divide, 15);
		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);

		associativity.put(TokenType.Multiply, Left);
		associativity.put(TokenType.Divide, Left);
		associativity.put(TokenType.Plus, Left);
		associativity.put(TokenType.Minus, Left);
	}

	public Parser(Token[] tokens) {
		this.tokens = tokens;
	}

	private void expected(TokenType t) {
		throw new Error("Unexpected token " + currentToken.type() + ". Expected " + t);
	}

	private Token consume() {
		final Token oldToken = currentToken;
		if (index + 1 != tokens.length) index++;
		currentToken = tokens[index];
		return oldToken;
	}

	private boolean match(TokenType t) {
		return currentToken.type() == t;
	}

	private Token require(TokenType t) {
		if (!match(t)) expected(t);
		return consume();
	}

	private Token accept(TokenType t) {
		return match(t) ? consume() : null;
	}

	public Program parse() {
		final Program program = new Program();
		consume();

		while (index < tokens.length) {
			if (currentToken.type() == TokenType.EOF) {
				break;
			} else if (currentToken.type() == TokenType.Terminator || currentToken.type() == TokenType.Semicolon) {
				consume();
			} else if (matchDeclaration()) {
				program.append(parseDeclaration());
			} else if (matchExpression()) {
				program.append(parseExpression(0, Left));
			} else {
				System.out.println("------- PARTIAL TREE -------");
				program.dump(0);
				System.out.println("------- ERROR -------");
				throw new NotImplementedException("Support for token '" + currentToken.type() + "'");
			}
		}

		return program;
	}

	private CallExpression parseCallExpression(Identifier identifier) {
		if (accept(TokenType.LParen) == null) return null;
		final Expression argument = parseExpression(0, Left);
		require(TokenType.RParen);
		return new CallExpression(identifier, argument);
	}

	private VariableDeclaration parseDeclaration() {
		require(TokenType.Let);
		final Token identifier = require(TokenType.Identifier);
		require(TokenType.Assign);
		final Expression value = parseExpression(0, Left);
		return new VariableDeclaration(new VariableDeclarator[] {
			new VariableDeclarator(new Identifier(identifier.value()), value)
		});
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) {
		Expression latestExpr = parsePrimaryExpression();

		while (matchSecondaryExpression()) {
			final int newPrecedence = precedence.get(currentToken.type());

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left) {
				break;
			}

			final Associativity newAssoc = associativity.get(currentToken.type());
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
		}

		return latestExpr;
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) {
		switch (currentToken.type()) {
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

			default: return left;
		}
	}

	private Expression parsePrimaryExpression() {
		return switch (currentToken.type()) {
			case LParen -> {
				consume();
				final Expression expression = parseExpression(0, Left);
				require(TokenType.RParen);
				yield expression;
			}

			case StringLiteral -> new StringLiteral(consume().value());
			case NumericLiteral -> new NumericLiteral(Double.parseDouble(consume().value()));
			case BooleanLiteral -> new BooleanLiteral(consume().value().equals("true"));

			case Identifier -> {
				final Identifier identifier = new Identifier(consume().value());
				final CallExpression callExpression = parseCallExpression(identifier);
				yield callExpression == null ? identifier : callExpression;
			}

			default -> throw new NotImplementedException("Expression type '" + currentToken.type() + "'");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type();
		return t == TokenType.Let;
	}

	private boolean matchExpression() {
		final TokenType t = currentToken.type();
		return t == TokenType.StringLiteral || t == TokenType.NumericLiteral ||
				t == TokenType.BooleanLiteral || t == TokenType.Identifier ||
				t == TokenType.LParen;
	}



	private boolean matchSecondaryExpression() {
		final TokenType t = currentToken.type();
		return t == TokenType.Plus || t == TokenType.Minus ||
				t == TokenType.Multiply || t == TokenType.Divide;
	}
}
