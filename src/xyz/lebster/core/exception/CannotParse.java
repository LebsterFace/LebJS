package xyz.lebster.core.exception;

import xyz.lebster.core.parser.Token;

public final class CannotParse extends ParseException {
	public CannotParse(String what) {
		super(what + " cannot be parsed.");
	}

	public CannotParse(Token token, String what) {
		super(token.type + " cannot be parsed as " + what + " (" + token.position + ")");
	}
}