package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

public record BooleanLiteral(BooleanValue value) implements Literal<BooleanValue> {
	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value == BooleanValue.TRUE ? "true" : "false");
	}
}
