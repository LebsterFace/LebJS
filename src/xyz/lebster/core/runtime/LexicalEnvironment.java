package xyz.lebster.core.runtime;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.object.ObjectLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;

public record LexicalEnvironment(ObjectLiteral variables, LexicalEnvironment parent) {
	public boolean hasBinding(StringLiteral name) {
		return variables.hasOwnProperty(name);
	}

	public void setVariable(Interpreter interpreter, StringLiteral name, Value<?> value) throws AbruptCompletion {
		variables.set(interpreter, name, value);
	}
}