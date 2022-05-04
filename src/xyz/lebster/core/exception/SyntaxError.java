package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

public final class SyntaxError extends ParseException {
	public SyntaxError(String message, SourcePosition position) {
		super(message, position);
	}
}