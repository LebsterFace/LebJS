package xyz.lebster.core.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.HashSet;

public class ErrorObject extends ObjectValue implements HasBuiltinTag {
	public final String message;

	public ErrorObject(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.errorPrototype);
		this.message = ANSI.stripFormatting(message);
		put(Names.message, new StringValue(message));
		put(Names.name, new StringValue(getClass().getSimpleName()));
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
	public final void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
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