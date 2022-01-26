package xyz.lebster.core.interpreter;

import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public record LexicalEnvironment(ObjectValue variables, LexicalEnvironment parent) {
	public boolean hasBinding(StringValue name) {
		return variables.hasOwnProperty(name);
	}

	public void setVariable(Interpreter interpreter, StringValue name, Value<?> value) throws AbruptCompletion {
		variables.set(interpreter, name, value);
	}
}