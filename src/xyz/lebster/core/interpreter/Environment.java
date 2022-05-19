package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

// TODO: Properly follow spec
public interface Environment {
	Environment parent();

	boolean hasBinding(StringValue name);

	Reference getBinding(Interpreter interpreter, StringValue name);

	void createBinding(Interpreter interpreter, StringValue name, Value<?> value);
}
