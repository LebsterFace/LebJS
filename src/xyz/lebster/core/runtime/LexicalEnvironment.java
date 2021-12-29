package xyz.lebster.core.runtime;

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

	public void setVariable(StringLiteral name, Value<?> value) {
		variables.set(name, value);
	}
}