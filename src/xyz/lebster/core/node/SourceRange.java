package xyz.lebster.core.node;

public final class SourceRange {
	private final String sourceText;
	public final int startIndex;
	public final int endIndex;
	private SourcePosition start;
	private SourcePosition end;

	public SourceRange(String sourceText, int startIndex, int endIndex) {
		this.sourceText = sourceText;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getText() {
		return sourceText.substring(sourceText.offsetByCodePoints(0, startIndex), sourceText.offsetByCodePoints(0, endIndex));
	}

	@Override
	public String toString() {
		return "(%s - %s)".formatted(start(), end());
	}

	public SourcePosition end() {
		if (end == null) end = new SourcePosition(sourceText, endIndex);
		return end;
	}

	public SourcePosition start() {
		if (start == null) start = new SourcePosition(sourceText, startIndex);
		return start;
	}
}