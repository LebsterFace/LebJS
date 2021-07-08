package xyz.lebster.exception;

import xyz.lebster.parser.Token;

public class CannotParse extends ParseException {
	public CannotParse(String what) {
		super(what + " cannot be parsed.");
	}

	public CannotParse(Token token, String what) {
		super(token.type + " cannot be parsed as " + what + " (index " + token.start + ")");
	}
}
