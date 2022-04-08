package xyz.lebster.core.node;

public record SourceRange(String sourceText, SourcePosition start, SourcePosition end) {
	public SourceRange(String sourceText, int startIndex, int endIndex) {
		this(sourceText, new SourcePosition(sourceText, startIndex), new SourcePosition(sourceText, endIndex));
	}

	public String getText() {
		return sourceText.substring(start.index, end.index);
	}
}