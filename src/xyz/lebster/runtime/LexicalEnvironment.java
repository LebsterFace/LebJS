package xyz.lebster.runtime;

import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;

public record LexicalEnvironment(Dictionary variables, LexicalEnvironment parent) {
	public boolean hasBinding(StringLiteral name) {
		return variables.hasOwnProperty(name);
	}

	public Value<?> getVariable(StringLiteral name) {
		return variables.get(name);
	}

	public Value<?> setVariable(StringLiteral name, Value<?> value) {
		return variables.set(name, value);
	}
}