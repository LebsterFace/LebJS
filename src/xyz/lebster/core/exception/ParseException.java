package xyz.lebster.core.exception;

abstract class ParseException extends Exception {
	public ParseException(String message) {
		super(message);
	}
}