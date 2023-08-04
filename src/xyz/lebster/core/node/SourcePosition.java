package xyz.lebster.core.node;

public final class SourcePosition {
	public final String sourceText;
	public final int index;
	private int line = -1;
	private int column = -1;

	public SourcePosition(String sourceText, int index) {
		this.sourceText = sourceText;
		this.index = index;
	}

	private void getLineAndColumn() {
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

	private int line() {
		if (line == -1) getLineAndColumn();
		return line;
	}

	private int column() {
		if (column == -1) getLineAndColumn();
		return column;
	}

	@Override
	public String toString() {
		return line() + ":" + column();
	}
}