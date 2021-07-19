package xyz.lebster.interpreter;

public final class StringRepresentation {
	private final StringBuilder builder = new StringBuilder();
	private int currentIndent = 0;

	public void append(Object o) {
		builder.append(o);
	}

	public int getCurrentIndent() {
		return currentIndent;
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