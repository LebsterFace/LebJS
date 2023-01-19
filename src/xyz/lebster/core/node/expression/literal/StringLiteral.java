package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.primitive.string.StringValue;

public record StringLiteral(StringValue value) implements Literal<StringValue> {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.value(this, StringEscapeUtils.quote(value.value, false));
	}

	@Override
	public void represent(StringRepresentation representation) {
		value.display(representation);
	}
}
