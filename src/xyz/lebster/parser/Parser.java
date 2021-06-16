package xyz.lebster.parser;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.value.StringLiteral;

public class Parser {
	public final Token[] tokens;
	private Token currentToken;
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

	private Expression disambiguateIdentifier() {
		final Identifier identifier = new Identifier(consume().value());
		final CallExpression callExpression = parseCallExpression(identifier);
		return callExpression == null ? identifier : callExpression;
	}

	public Program parse() {
		final Program program = new Program();
		consume();

		while (index < tokens.length) {
			if (matchDeclaration()) {
				program.append(parseDeclaration());
			} else if (currentToken.type() == TokenType.Identifier) {
				program.append(disambiguateIdentifier());
			} else {
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
			case Identifier -> disambiguateIdentifier();
			default -> throw new NotImplementedException("Expression type '" + currentToken.type() + "'");
		};
	}

	private boolean matchDeclaration() {
		final TokenType t = currentToken.type();
		return t == TokenType.Let;
	}
}
