package xyz.lebster.core.parser;

public final class Token {
	public final TokenType type;
	public final String value;
	public final int start;
	public final int end;

	public Token(TokenType type, String value, int start, int end) {
		this.type = type;
		this.value = value;
		this.start = start;
		this.end = end;
	}

	public Token(TokenType type, int start, int end) {
		this(type, null, start, end);
	}

	@Override
	public String toString() {
		return value == null ? String.valueOf(type) : '"' + StringEscapeUtils.escape(value) + "\" (" + type + ")";
	}
}