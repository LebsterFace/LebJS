package xyz.lebster.interpreter;

import xyz.lebster.node.BlockStatement;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;

public record ScopeFrame(Dictionary variables, BlockStatement node) {
	public boolean containsVariable(StringLiteral name) {
		return variables.containsKey(name);
	}

	public boolean containsVariable(String name) {
		return variables.containsKey(name);
	}

	public Value<?> getVariable(StringLiteral name) {
		return variables.get(name);
	}

	public Value<?> getVariable(String name) {
		return variables.get(name);
	}

	public ScopeFrame setVariable(StringLiteral name, Value<?> value) {
		variables.set(name, value);
		return this;
	}

	public ScopeFrame setVariable(String name, Value<?> value) {
		variables.set(name, value);
		return this;
	}
}
