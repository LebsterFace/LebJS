package xyz.lebster.core.parser;

import xyz.lebster.core.exception.SyntaxError;

public final class ParserState implements Cloneable {
	public final Token[] tokens;
	public Token currentToken;
	public int index = -1;

	public ParserState(Token[] tokens) {
		this.tokens = tokens;
	}

	private ParserState(Token[] tokens, int index, Token currentToken) {
		this.tokens = tokens;
		this.currentToken = currentToken;
		this.index = index;
	}

	public void expected(TokenType t) throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken.type + ". Expected " + t);
	}

	public void expected(String t) throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken.type + ". Expected " + t);
	}

	public void unexpected() throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken.type + ".");
	}

	Token consume() {
		final Token oldToken = currentToken;
		if (index + 1 != tokens.length) index++;
		currentToken = tokens[index];
		return oldToken;
	}

	Token require(TokenType t) throws SyntaxError {
		if (currentToken.type != t) expected(t);
		return consume();
	}

	Token accept(TokenType t) {
		return currentToken.type == t ? consume() : null;
	}

	void consumeAll(TokenType t) {
		while (currentToken.type == t) consume();
	}

	@Override
	public ParserState clone() {
		return new ParserState(tokens, index, currentToken);
	}
}