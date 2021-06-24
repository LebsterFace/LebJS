package xyz.lebster.exception;

import xyz.lebster.parser.TokenType;

public class CannotParse extends ParseException {
	public CannotParse(String what) {
		super(what + " cannot be parsed.");
	}

	public CannotParse(TokenType type, String what) {
		super(type + " cannot be parsed as " + what);
	}
}
