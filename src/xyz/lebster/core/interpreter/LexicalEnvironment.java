package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

public record LexicalEnvironment(ObjectValue variables, Environment parent) implements Environment {
	public boolean hasBinding(StringValue name) {
		return variables.hasOwnProperty(name);
	}

	@Override
	public Reference getBinding(Interpreter interpreter, StringValue name) {
		return new Reference(variables, name);
	}

	@Override
	public void createBinding(Interpreter interpreter, StringValue name, Value<?> value) {
		variables.put(name, value);
	}
}