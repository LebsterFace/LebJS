package xyz.lebster.parser;

import xyz.lebster.core.node.*;
import xyz.lebster.core.value.StringLiteral;

public class Parser {
	public final Token[] tokens;
	private Token currentToken;
	private Program program;
	private int index = -1;

	public Parser(Token[] tokens) {
		this.tokens = tokens;
	}

	private void expected(TokenType t) {
		throw new Error("Unexpected token " + currentToken.type() + ". Expected " + t);
	}

	private Token consume() {
		index++;
		if (index == tokens.length) {
			currentToken = null;
			return null;
		} else {
			final Token oldToken = currentToken;
			currentToken = tokens[index];
			return oldToken;
		}
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
		program = new Program();
		consume();

		while (index < tokens.length) {
			if (matchDeclaration()) {
				program.append(parseDeclaration());
			} else if (currentToken.type() == TokenType.Identifier) {
				// Copied from Parser.parseExpression[case: Identifier]
				final Identifier identifier = new Identifier(consume().value());
				final CallExpression callExpression = parseCallExpression(identifier);
				program.append(callExpression == null ? identifier : callExpression);
			} else {
				throw new Error("I can't handle this!!!"); // YOU CAN'T HANDLE THE TRUTH!
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
			case Identifier -> {
				final Identifier identifier = new Identifier(consume().value());
				final CallExpression callExpression = parseCallExpression(identifier);
				yield callExpression == null ? identifier : callExpression;
			}

			default -> throw new Error("Unsupported expression type");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type();
		return t == TokenType.Let;
	}
}
