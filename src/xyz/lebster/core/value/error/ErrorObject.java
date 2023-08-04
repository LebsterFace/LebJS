package xyz.lebster.core.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
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
		put(Names.name, new StringValue(getName()));
	}

	public ErrorObject(Interpreter interpreter, String message) {
		this(interpreter.intrinsics.errorPrototype, message);
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.BRIGHT_CYAN);
		builder.append("[");
		builder.append(getName());
		builder.append(": ");
		builder.append(message);
		builder.append("]");
		builder.append(ANSI.RESET);
	}

	protected String getName() {
		return getClass() == ErrorObject.class ? "Error" : getClass().getSimpleName();
	}

	@Override
	public boolean displayAsJSON() {
		return false;
	}

	@Override
	public String toString() {
		return getName() + ": " + message;
	}

	@Override
	public final String getBuiltinTag() {
		return "Error";
	}
}