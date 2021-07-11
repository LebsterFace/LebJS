package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.StringRepresentation;

public abstract class StaticIdentifier implements Expression {
	public abstract String getName();

	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", getName());
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(getName());
	}
}