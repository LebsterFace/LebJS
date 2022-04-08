package xyz.lebster.core.parser;

import xyz.lebster.core.node.SourcePosition;

public final class Token {
	public final TokenType type;
	public final String value;

	public SourcePosition position;

	public Token(TokenType type, String value, SourcePosition position) {
		this.type = type;
		this.value = value;
		this.position = position;
	}

	public Token(TokenType type, SourcePosition position) {
		this.type = type;
		this.value = null;
		this.position = position;
	}

	@Override
	public String toString() {
		return value == null ? String.valueOf(type) : '"' + StringEscapeUtils.escape(value) + "\" (" + type + ")";
	}
}