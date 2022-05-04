package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

abstract class ParseException extends Exception {
	public ParseException(String message, SourcePosition position) {
		super(message + " (" + position + ")");
	}
}