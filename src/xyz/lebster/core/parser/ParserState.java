package xyz.lebster.core.parser;

import xyz.lebster.core.exception.SyntaxError;

public final class ParserState {
	public final Token[] tokens;
	public Token token;
	public int index = -1;
	public boolean inBreakContext = false;
	public boolean inContinueContext = false;

	public ParserState(Token[] tokens) {
		this.tokens = tokens;
		this.consume();
	}

	private ParserState(Token[] tokens, int index, Token token) {
		this.tokens = tokens;
		this.token = token;
		this.index = index;
	}

	public void expected(TokenType type) throws SyntaxError {
		throw new SyntaxError("Unexpected token %s. Expected %s".formatted(token, type), token.position);
	}

	public void expected(String value) throws SyntaxError {
		throw new SyntaxError("Unexpected token %s. Expected '%s'".formatted(token, value), token.position);
	}

	public void unexpected() throws SyntaxError {
		throw new SyntaxError("Unexpected token " + token, token.position);
	}

	Token consume() {
		final Token oldToken = token;
		if (index + 1 != tokens.length) index++;
		token = tokens[index];
		return oldToken;
	}

	String require(TokenType type) throws SyntaxError {
		if (token.type != type) expected(type);
		return consume().value;
	}

	void require(TokenType type, String value) throws SyntaxError {
		if (token.type != type) expected(type);
		if (!token.value.equals(value)) expected(value);
		consume();
	}

	Token accept(TokenType type) {
		return token.type == type ? consume() : null;
	}

	boolean match(TokenType type, String value) {
		return token.type == type && token.value.equals(value);
	}

	boolean optional(TokenType type) {
		if (token.type == type) {
			consume();
			return true;
		}

		return false;
	}

	boolean optional(TokenType... types) {
		if (is(types)) {
			consume();
			return true;
		}

		return false;
	}

	boolean is(TokenType... types) {
		for (final TokenType type : types) {
			if (token.type == type)
				return true;
		}

		return false;
	}

	boolean is(TokenType type) {
		return token.type == type;
	}

	void consumeAll(TokenType t) {
		while (token.type == t) consume();
	}

	boolean isFinished() {
		return index >= tokens.length;
	}

	public ParserState copy() {
		final ParserState cloned = new ParserState(tokens, index, token);
		cloned.inContinueContext = this.inContinueContext;
		cloned.inBreakContext = this.inBreakContext;
		return cloned;
	}
}