package xyz.lebster.core.interpreter.environment;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public class DeclarativeEnvironment implements Environment {
	private final ObjectValue variables;
	private final Environment parent;

	public DeclarativeEnvironment(Environment parent) {
		this.variables = new ObjectValue(null);
		this.parent = parent;
	}

	@Override
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

	@Override
	public Environment parent() {
		return parent;
	}
}