package xyz.lebster.core;

import java.util.Set;

public final class StringEscapeUtils {
	private static String hex(char character) {
		return Integer.toHexString(character).toUpperCase();
	}

	public static String escape(String str, Set<Character> additionalEscapes) {
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
					case 0x0B -> res.append('\\').append('v'); // \x0B -> \v
					default -> {
						res.append("\\x");
						if (ch <= 0xf) res.append('0');
						res.append(hex(ch));
					}
				}
			} else {
				if (additionalEscapes != null && additionalEscapes.contains(ch)) res.append('\\');
				res.append(ch);
			}
		}

		return res.toString();
	}

	public static String quote(String str, boolean preferDoubleQuotes) {
		final boolean containsDoubleQuotes = str.indexOf('"') != -1;
		final boolean containsSingleQuotes = str.indexOf('\'') != -1;

		if (containsSingleQuotes && containsDoubleQuotes) {
			final char quoteType = preferDoubleQuotes ? '"' : '\'';
			return quoteType + escape(str, Set.of(quoteType)) + quoteType;
		}

		final char quoteType;
		if (containsDoubleQuotes) quoteType = '\'';
		else if (containsSingleQuotes) quoteType = '"';
		else quoteType = preferDoubleQuotes ? '"' : '\'';
		return quoteType + escape(str, null) + quoteType;
	}
}