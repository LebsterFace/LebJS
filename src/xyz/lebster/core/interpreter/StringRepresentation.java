package xyz.lebster.core.interpreter;

import xyz.lebster.core.ANSI;

public final class StringRepresentation implements CharSequence {
	private final StringBuilder builder = new StringBuilder();
	private int currentIndent = 0;

	public void append(String s) {
		builder.append(s);
	}

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
		return ANSI.stripFormatting(this.builder.toString()).length();
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