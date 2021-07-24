package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;

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