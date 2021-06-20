package xyz.lebster.exception;

public class CannotParse extends ParseException {
	public CannotParse(String message) {
		super(message + " cannot be parsed.");
	}
}
