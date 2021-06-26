package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;

public abstract class StaticIdentifier implements Expression {
	public abstract String getName();

	public void dump(int indent) {
		Interpreter.dumpValue(indent, "Identifier", getName());
	}
}
