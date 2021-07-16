package xyz.lebster.exception;

abstract class ParseException extends Exception {
	public ParseException(String message) {
		super(message);
	}
}