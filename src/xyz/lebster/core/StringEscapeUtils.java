package xyz.lebster.core;

public final class StringEscapeUtils {
	private static void appendHex(StringBuilder builder, int ch) {
		if (ch < 0x10) builder.append('0');
		builder.append(Integer.toHexString(ch).toUpperCase());
	}

	private static void appendUnicode(StringBuilder builder, int ch) {
		if (ch < 0x1000) builder.append('0');
		if (ch < 0x100) builder.append('0');
		if (ch < 0x10) builder.append('0');
		builder.append(Integer.toHexString(ch).toUpperCase());
	}

	private static boolean shouldEscape(int codePoint) {
		if (!Character.isValidCodePoint(codePoint)) return false;
		final int type = Character.getType(codePoint);
		return type == Character.UNASSIGNED ||
			   type == Character.PRIVATE_USE ||
			   type == Character.SURROGATE ||
			   (codePoint & 0xFFFE) == 0xFFFE || // noncharacters
			   (codePoint >= 0xFDD0 && codePoint <= 0xFDEF); // also noncharacters
	}

	public static String escape(String str, char quoteType) {
		if (str == null) return null;
		final StringBuilder res = new StringBuilder(str.length() * 2);

		for (final int c : str.codePoints().toArray()) {
			if (c < 32 || (c >= 127 && c <= 159)) {
				res.append('\\');
				switch (c) {
					case '\b' -> res.append('b');
					case '\n' -> res.append('n');
					case '\t' -> res.append('t');
					case '\f' -> res.append('f');
					case '\r' -> res.append('r');
					case 0x0B -> res.append('v');
					default -> {
						res.append('x');
						appendHex(res, c);
					}
				}
			} else if (shouldEscape(c)) {
				res.append("\\u");
				if (c > 0xffff) res.append('{');
				appendUnicode(res, c);
				if (c > 0xffff) res.append('}');
			} else {
				if (c == '\\' || c == quoteType) {
					res.append('\\');
				}

				res.appendCodePoint(c);
			}
		}

		return res.toString();
	}

	public static String quote(String str, boolean preferDoubleQuotes) {
		final boolean containsDoubleQuotes = str.indexOf('"') != -1;
		final boolean containsSingleQuotes = str.indexOf('\'') != -1;

		final char quoteType;
		if (containsSingleQuotes == containsDoubleQuotes) {
			quoteType = preferDoubleQuotes ? '"' : '\'';
		} else if (containsDoubleQuotes) {
			quoteType = '\'';
		} else {
			quoteType = '"';
		}

		return quoteType + escape(str, quoteType) + quoteType;
	}
}