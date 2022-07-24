package xyz.lebster.core.node;

public final class SourcePosition {
	final int index;
	private final int line;
	private final int column;

	public SourcePosition(String sourceText, int index) {
		this.index = index;

		int indexOfLastNewline = -1;
		int lineNumber = 1;
		for (int i = 0; i < index && i < sourceText.length(); i++) {
			if (sourceText.charAt(i) == '\n') {
				indexOfLastNewline = i;
				lineNumber++;
			}
		}

		this.line = lineNumber;
		this.column = index - indexOfLastNewline;
	}

	@Override
	public String toString() {
		return line + ":" + column;
	}
}