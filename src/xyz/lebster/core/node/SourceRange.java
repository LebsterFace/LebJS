package xyz.lebster.core.node;

public record SourceRange(String sourceText, int startIndex, int endIndex) {
	public SourceRange(String sourceText, SourcePosition start, SourcePosition end) {
		this(sourceText, start.index, end.index);
	}

	public String getText() {
		return sourceText.substring(startIndex, endIndex);
	}
}