package xyz.lebster.exception;

public class CannotParse extends ParseError {
	public CannotParse(String message) {
		super(message + " cannot be parsed.");
	}
}
