package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.number.NumberValue;

public record NumericLiteral(NumberValue value) implements Literal<NumberValue> {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.value(this, value.stringValueOf());
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value.stringValueOf());
	}
}
