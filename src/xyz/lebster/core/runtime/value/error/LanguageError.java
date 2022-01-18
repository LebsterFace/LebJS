package xyz.lebster.core.runtime.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

import java.util.HashSet;

public abstract class LanguageError extends ObjectValue {
	public final String message;

	public LanguageError(String message) {
		this.message = message;
		put("message", new StringValue(message));
		put("name", new StringValue(getClass().getSimpleName()));
	}

	@Override
	public final void display(StringRepresentation builder) {
		builder.append(ANSI.BRIGHT_CYAN);
		builder.append("[");
		builder.append(getClass().getSimpleName());
		builder.append(": ");
		builder.append(message);
		builder.append("]");
		builder.append(ANSI.RESET);
	}

	@Override
	public final void displayRecursive(StringRepresentation builder, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(builder);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}