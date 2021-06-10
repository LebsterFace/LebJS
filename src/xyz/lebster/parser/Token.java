package xyz.lebster.parser;

import java.util.Objects;

public class Token {
	public final TokenType type;
	public final String value;

	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	public Token(TokenType type, StringBuffer value) {
		this(type, value.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Token token = (Token) o;
		return type == token.type && Objects.equals(value, token.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}
}
