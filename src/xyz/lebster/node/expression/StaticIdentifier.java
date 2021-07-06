package xyz.lebster.node.expression;

import xyz.lebster.Dumper;

public abstract class StaticIdentifier implements Expression {
	public abstract String getName();

	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", getName());
	}
}
