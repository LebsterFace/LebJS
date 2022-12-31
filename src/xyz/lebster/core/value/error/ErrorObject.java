package xyz.lebster.core.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public class ErrorObject extends ObjectValue implements HasBuiltinTag {
	public final String message;

	public ErrorObject(ObjectValue prototype, String message) {
		super(prototype);
		this.message = ANSI.stripFormatting(message);
		put(Names.message, new StringValue(message));
		put(Names.name, getClass() == ErrorObject.class ? Names.Error : new StringValue(getClass().getSimpleName()));
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append("[");
		representation.append(getClass().getSimpleName());
		representation.append(": ");
		representation.append(message);
		representation.append("]");
		representation.append(ANSI.RESET);
	}

	@Override
	public boolean displayAsJSON() {
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}

	@Override
	public final String getBuiltinTag() {
		return "Error";
	}
}