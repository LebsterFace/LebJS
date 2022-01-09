package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.NullValue;

public final class NullLiteral implements Literal<NullValue> {
	@Override
	public NullValue value() {
		return NullValue.instance;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "NullLiteral");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("null");
	}
}
