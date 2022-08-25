package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

abstract class ParseException extends Exception {
	private final SourcePosition position;

	public ParseException(String message, SourcePosition position) {
		super(message);
		this.position = position;
	}

	@Override
	public String toString() {
		return super.getMessage() + " (" + position + ")";
	}
}