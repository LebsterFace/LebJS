package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

public record BindingPair(StringValue name, Value<?> value) {
	public void declare(Interpreter interpreter) throws AbruptCompletion {
		interpreter.declareVariable(name, value);
	}
}
