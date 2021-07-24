package xyz.lebster.core.parser;

public final class StringEscapeUtils {
	private static String hex(char character) {
		return Integer.toHexString(character).toUpperCase();
	}

	public static String escape(String str) {
		if (str == null) return null;
		final int length = str.length();
		final StringBuilder res = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			final char ch = str.charAt(i);
			if (ch > 0xfff) {
				res.append("\\u").append(hex(ch));
			} else if (ch > 0xff) {
				res.append("\\u0").append(hex(ch));
			} else if (ch > 0x7f) {
				res.append("\\u00").append(hex(ch));
			} else if (ch < 32) {
				switch (ch) {
					case '\b' -> res.append('\\').append('b');
					case '\n' -> res.append('\\').append('n');
					case '\t' -> res.append('\\').append('t');
					case '\f' -> res.append('\\').append('f');
					case '\r' -> res.append('\\').append('r');
					default -> {
						res.append("\\u00");
						if (ch <= 0xf) res.append('0');
						res.append(hex(ch));
					}
				}
			} else {
				res.append(ch);
			}
		}

		return res.toString();
	}
}