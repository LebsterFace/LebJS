package xyz.lebster.parser;

public record Token(TokenType type, String value, int start, int end) {
	@Override
	public String toString() {
		return "Token [" +
			"type=" + type +
			", value='" + StringEscapeUtils.escapeJavaString(value) + '\'' +
			", range=" + start + "," + end +
		']';
	}
}
