package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

public final class ParserNotImplemented extends RuntimeException {
	public ParserNotImplemented(SourcePosition position, String message) {
		super(message + " has not been implemented (" + position + ").");
	}
}