package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

public final class SyntaxError extends Exception {
	public SyntaxError(String message, SourcePosition position) {
		super("%s (%s)".formatted(message, position));
	}
}