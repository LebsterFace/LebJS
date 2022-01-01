package xyz.lebster.core.runtime;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;

public class LanguageError extends Dictionary {
	public final String message;

	public LanguageError(String message) {
		this.message = message;
		put("message", new StringLiteral(message));
		put("name", new StringLiteral(getClass().getSimpleName()));
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