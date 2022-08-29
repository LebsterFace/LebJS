package xyz.lebster.core.interpreter.environment;

import xyz.lebster.core.interpreter.GlobalObject;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public record GlobalEnvironment(ObjectValue variables, GlobalObject globalObject) implements Environment {
	public GlobalEnvironment(GlobalObject globalObject) {
		this(new ObjectValue((ObjectValue) null), globalObject);
	}

	@Override
	public Environment parent() {
		return null;
	}

	public boolean hasBinding(StringValue name) {
		return variables.hasProperty(name) || globalObject.hasProperty(name);
	}

	@Override
	public Reference getBinding(Interpreter interpreter, StringValue name) {
		return new Reference(variables.hasOwnProperty(name) ? variables : globalObject, name);
	}

	@Override
	public void createBinding(Interpreter interpreter, StringValue name, Value<?> value) {
		variables.put(name, value);
	}
}