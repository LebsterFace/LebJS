package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.BooleanValue;

public record BooleanLiteral(BooleanValue value) implements Literal<BooleanValue> {
	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, value.type.name(), value == BooleanValue.TRUE ? "true" : "false");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value == BooleanValue.TRUE ? "true" : "false");
	}
}