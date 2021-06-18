package xyz.lebster.parser;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.StringLiteral;

import java.util.HashMap;

public class Parser {
	public final Token[] tokens;
	private Token currentToken;
	private int index = -1;

	private static final HashMap<TokenType, Integer> precedence = new HashMap<>();
	private static final HashMap<TokenType, Boolean> associativity = new HashMap<>();

	static {
//		https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table
		precedence.put(TokenType.Multiply, 15);
		precedence.put(TokenType.Divide, 15);
		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);

//		true = left, false = right
		associativity.put(TokenType.Multiply, true);
		associativity.put(TokenType.Divide, true);
		associativity.put(TokenType.Plus, true);
		associativity.put(TokenType.Minus, true);
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
				program.append(parseExpression());
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
		final Expression argument = parseExpression();
		require(TokenType.RParen);
		return new CallExpression(identifier, argument);
	}

	private VariableDeclaration parseDeclaration() {
		require(TokenType.Let);
		final Token identifier = require(TokenType.Identifier);
		require(TokenType.Assign);
		final Expression value = parseExpression();
		return new VariableDeclaration(new VariableDeclarator[] {
			new VariableDeclarator(new Identifier(identifier.value()), value)
		});
	}

	private Expression parseExpression() {
		return switch (currentToken.type()) {
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
				t == TokenType.BooleanLiteral || t == TokenType.Identifier;
	}
}
