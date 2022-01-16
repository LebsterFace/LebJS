package xyz.lebster.core.interpreter;

import xyz.lebster.core.ANSI;

public final class StringRepresentation implements CharSequence {
	private final StringBuilder builder = new StringBuilder();
	private int currentIndent = 0;

	public void append(Object o) {
		builder.append(o);
	}

	public void appendLine(Object o) {
		append(o);
		appendLine();
	}

	public void appendLine() {
		builder.append('\n');
	}

	public void indent() {
		currentIndent++;
	}

	public int length() {
		return this.builder.toString()
			.replace(ANSI.BLACK, "")
			.replace(ANSI.RED, "")
			.replace(ANSI.GREEN, "")
			.replace(ANSI.YELLOW, "")
			.replace(ANSI.BLUE, "")
			.replace(ANSI.MAGENTA, "")
			.replace(ANSI.CYAN, "")
			.replace(ANSI.WHITE, "")
			.replace(ANSI.RESET, "")
			.replace(ANSI.BRIGHT_BLACK, "")
			.replace(ANSI.BRIGHT_RED, "")
			.replace(ANSI.BRIGHT_GREEN, "")
			.replace(ANSI.BRIGHT_YELLOW, "")
			.replace(ANSI.BRIGHT_BLUE, "")
			.replace(ANSI.BRIGHT_MAGENTA, "")
			.replace(ANSI.BRIGHT_CYAN, "")
			.replace(ANSI.BRIGHT_WHITE, "")
			.replace(ANSI.BACKGROUND_BLACK, "")
			.replace(ANSI.BACKGROUND_RED, "")
			.replace(ANSI.BACKGROUND_GREEN, "")
			.replace(ANSI.BACKGROUND_YELLOW, "")
			.replace(ANSI.BACKGROUND_BLUE, "")
			.replace(ANSI.BACKGROUND_MAGENTA, "")
			.replace(ANSI.BACKGROUND_CYAN, "")
			.replace(ANSI.BACKGROUND_WHITE, "")
			.replace(ANSI.BACKGROUND_BRIGHT_BLACK, "")
			.replace(ANSI.BACKGROUND_BRIGHT_RED, "")
			.replace(ANSI.BACKGROUND_BRIGHT_GREEN, "")
			.replace(ANSI.BACKGROUND_BRIGHT_YELLOW, "")
			.replace(ANSI.BACKGROUND_BRIGHT_BLUE, "")
			.replace(ANSI.BACKGROUND_BRIGHT_MAGENTA, "")
			.replace(ANSI.BACKGROUND_BRIGHT_CYAN, "")
			.replace(ANSI.BACKGROUND_BRIGHT_WHITE, "")
			.replace(ANSI.BOLD, "")
			.replace(ANSI.UNDERLINE, "")
			.replace(ANSI.REVERSED, "")
			.length();
	}

	@Override
	public char charAt(int index) {
		return this.builder.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.builder.subSequence(start, end);
	}

	public void unindent() {
		currentIndent--;
	}

	public void appendIndent() {
		builder.append("\t".repeat(currentIndent));
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}