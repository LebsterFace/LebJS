package xyz.lebster.core.parser;

import xyz.lebster.core.exception.SyntaxError;

public final class ParserState implements Cloneable {
	public final Token[] tokens;
	public Token currentToken;
	public int index = -1;
	public boolean inBreakContext = false;
	public boolean inContinueContext = false;

	public ParserState(Token[] tokens) {
		this.tokens = tokens;
	}

	private ParserState(Token[] tokens, int index, Token currentToken) {
		this.tokens = tokens;
		this.currentToken = currentToken;
		this.index = index;
	}

	public void expected(TokenType type) throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken + ". Expected " + type);
	}

	public void expected(String value) throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken + ". Expected '" + value + "'");
	}

	public void unexpected() throws SyntaxError {
		throw new SyntaxError("Unexpected token " + currentToken + ".");
	}

	Token consume() {
		final Token oldToken = currentToken;
		if (index + 1 != tokens.length) index++;
		currentToken = tokens[index];
		return oldToken;
	}

	String require(TokenType type) throws SyntaxError {
		if (currentToken.type != type) expected(type);
		return consume().value;
	}

	void require(TokenType type, String value) throws SyntaxError {
		if (currentToken.type != type) expected(type);
		if (!currentToken.value.equals(value)) expected(value);
		consume();
	}

	Token accept(TokenType type) {
		return currentToken.type == type ? consume() : null;
	}

	boolean match(TokenType type, String value) {
		return currentToken.type == type && currentToken.value.equals(value);
	}

	void consumeAll(TokenType t) {
		while (currentToken.type == t) consume();
	}

	@Override
	public ParserState clone() {
		final ParserState cloned = new ParserState(tokens, index, currentToken);
		cloned.inContinueContext = this.inContinueContext;
		cloned.inBreakContext = this.inBreakContext;
		return cloned;
	}
}