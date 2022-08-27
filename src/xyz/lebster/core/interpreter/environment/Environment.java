package xyz.lebster.core.interpreter.environment;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

// TODO: Properly follow spec
public interface Environment {
	Environment parent();

	boolean hasBinding(StringValue name);

	Reference getBinding(Interpreter interpreter, StringValue name);

	void createBinding(Interpreter interpreter, StringValue name, Value<?> value);
}
