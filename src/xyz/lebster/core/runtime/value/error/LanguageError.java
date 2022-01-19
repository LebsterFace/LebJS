package xyz.lebster.core.runtime.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

import java.util.HashSet;

public abstract class LanguageError extends ObjectValue {
	public final String message;

	public LanguageError(String message) {
		this.message = ANSI.stripFormatting(message);
		put("message", new StringValue(message));
		put("name", new StringValue(getClass().getSimpleName()));
	}

	@Override
	public final void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append("[");
		representation.append(getClass().getSimpleName());
		representation.append(": ");
		representation.append(message);
		representation.append("]");
		representation.append(ANSI.RESET);
	}

	@Override
	public final void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}