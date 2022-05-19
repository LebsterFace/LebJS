package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.boolean_.BooleanValue;

public record BooleanLiteral(BooleanValue value) implements Literal<BooleanValue> {
	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Boolean", value == BooleanValue.TRUE ? "true" : "false");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value == BooleanValue.TRUE ? "true" : "false");
	}
}
