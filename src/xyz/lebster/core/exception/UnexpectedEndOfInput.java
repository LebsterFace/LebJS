package xyz.lebster.core.exception;

public final class UnexpectedEndOfInput extends SyntaxError {
	public UnexpectedEndOfInput(String expected) {
		super(expected);
	}
}