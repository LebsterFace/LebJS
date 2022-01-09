package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.NumberValue;

public record NumericLiteral(NumberValue value) implements Literal<NumberValue> {
	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, value.type.name(), value.stringValueOf());
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value.stringValueOf());
	}
}
