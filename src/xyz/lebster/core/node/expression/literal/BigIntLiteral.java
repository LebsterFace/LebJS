package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;

public record BigIntLiteral(BigIntValue value) implements Literal<BigIntValue> {
	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value.value.toString());
		representation.append('n');
	}
}
