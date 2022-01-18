package xyz.lebster.core.runtime.value.error;

import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.object.ObjectValue;

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