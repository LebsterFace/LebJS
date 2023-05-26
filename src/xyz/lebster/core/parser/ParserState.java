package xyz.lebster.core.parser;

import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.SourcePosition;
import xyz.lebster.core.node.expression.ObjectExpression;
import xyz.lebster.core.node.expression.literal.StringLiteral;

import java.util.HashMap;

public final class ParserState {
	public final Token[] tokens;
	public Token token;
	public int index = -1;
	public boolean inBreakContext = false;
	public boolean inContinueContext = false;
	public HashMap<ObjectExpression, SourcePosition> invalidProperties = new HashMap<>();

	public ParserState(Token[] tokens) {
		this.tokens = tokens;
		this.consume();
	}

	private ParserState(Token[] tokens, int index, Token token) {
		this.tokens = tokens;
		this.token = token;
		this.index = index;
	}

	public SyntaxError expected(TokenType type) {
		return new SyntaxError("Unexpected token %s. Expected %s".formatted(token, type), token.position);
	}

	public SyntaxError expected(String value) {
		return new SyntaxError("Unexpected token %s. Expected '%s'".formatted(token, value), token.position);
	}

	public SyntaxError unexpected() {
		return unexpected(this.token);
	}

	public SyntaxError unexpected(Token unexpectedToken) {
		return new SyntaxError("Unexpected token " + unexpectedToken, unexpectedToken.position);
	}

	Token consume() {
		final Token oldToken = token;
		if (index + 1 != tokens.length) index++;
		token = tokens[index];
		return oldToken;
	}

	String require(TokenType type) throws SyntaxError {
		if (token.type != type) throw expected(type);
		return consume().value;
	}

	void requireIdentifier(String value) throws SyntaxError {
		if (token.type != TokenType.Identifier) throw expected(TokenType.Identifier);
		if (!token.value.equals(value)) throw expected(value);
		consume();
	}

	Token accept(TokenType type) {
		return token.type == type ? consume() : null;
	}

	boolean optional(TokenType type) {
		if (token.type == type) {
			consume();
			return true;
		}

		return false;
	}

	boolean optional(TokenType type, String value) {
		if (token.type == type && token.value.equals(value)) {
			consume();
			return true;
		}

		return false;
	}

	StringLiteral optionalStringLiteral(TokenType type) {
		final Token token = accept(type);
		if (token == null) return null;
		return token.asStringLiteral();
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

	boolean is(TokenType type, String value) {
		return token.type == type && token.value.equals(value);
	}

	void consumeAll(TokenType t) {
		while (token.type == t) consume();
	}

	public ParserState copy() {
		final ParserState cloned = new ParserState(tokens, index, token);
		cloned.inContinueContext = this.inContinueContext;
		cloned.inBreakContext = this.inBreakContext;
		return cloned;
	}
}