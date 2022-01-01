package xyz.lebster.core.runtime;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;

public record LexicalEnvironment(Dictionary variables, LexicalEnvironment parent) {
	public boolean hasBinding(StringLiteral name) {
		return variables.hasOwnProperty(name);
	}

	public Value<?> getVariable(StringLiteral name) {
		return variables.get(name);
	}

	public void setVariable(Interpreter interpreter, StringLiteral name, Value<?> value) throws AbruptCompletion {
		variables.set(interpreter, name, value);
	}
}