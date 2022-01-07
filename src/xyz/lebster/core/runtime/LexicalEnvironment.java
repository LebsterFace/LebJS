package xyz.lebster.core.runtime;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Value;

public record LexicalEnvironment(ObjectValue variables, LexicalEnvironment parent) {
	public boolean hasBinding(StringValue name) {
		return variables.hasOwnProperty(name);
	}

	public void setVariable(Interpreter interpreter, StringValue name, Value<?> value) throws AbruptCompletion {
		variables.set(interpreter, name, value);
	}
}