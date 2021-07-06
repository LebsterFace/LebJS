package xyz.lebster.runtime;

import xyz.lebster.Dumper;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;

public class LanguageError extends Dictionary {
	public final String message;

	public LanguageError(String message) {
		this.message = message;
		set("message", new StringLiteral(message));
		set("name", new StringLiteral(getClass().getSimpleName()));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, getClass().getSimpleName(), message);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}
