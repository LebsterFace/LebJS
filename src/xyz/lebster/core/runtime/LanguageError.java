package xyz.lebster.core.runtime;

import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.node.value.StringValue;

public class LanguageError extends ObjectValue {
	public final String message;

	public LanguageError(String message) {
		this.message = message;
		put("message", new StringValue(message));
		put("name", new StringValue(getClass().getSimpleName()));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}