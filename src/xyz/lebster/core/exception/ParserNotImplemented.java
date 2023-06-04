package xyz.lebster.core.exception;

import xyz.lebster.core.node.SourcePosition;

public final class ParserNotImplemented extends RuntimeException {
	public ParserNotImplemented(SourcePosition position, String message) {
		super("Parsing %s has not been implemented (%s).".formatted(message, position));
	}
}