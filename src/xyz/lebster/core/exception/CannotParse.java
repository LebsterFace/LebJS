package xyz.lebster.core.exception;

import xyz.lebster.core.parser.Token;

public final class CannotParse extends ParseException {
	public CannotParse(Token token) {
		super("Token '%s' cannot be parsed (%s).".formatted(token.type, token.position));
	}

	public CannotParse(Token token, String what) {
		super("%s cannot be parsed as %s (%s)".formatted(token.type, what, token.position));
	}
}