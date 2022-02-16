package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.primitive.NumberValue;

public record NumericLiteral(NumberValue value) implements Literal<NumberValue> {
	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Number", value.stringValueOf());
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value.stringValueOf());
	}
}
