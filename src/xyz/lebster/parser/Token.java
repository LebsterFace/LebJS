package xyz.lebster.parser;

public class Token {
	public final TokenType type;
	public final String value;
	public final String string;
	public final int start;
	public final int end;

	public Token(TokenType type, String value, int start, int end) {
		this.type = type;
		this.value = value;
		this.start = start;
		this.end = end;
		this.string = value == null ? String.valueOf(type) : type + ": \"" + StringEscapeUtils.escape(value) + '"';
	}

	public Token(TokenType type, int start, int end) {
		this(type, null, start, end);
	}

	@Override
	public String toString() {
		return string;
	}
}
