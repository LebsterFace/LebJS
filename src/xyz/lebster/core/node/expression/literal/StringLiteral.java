package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.primitive.string.StringValue;

public record StringLiteral(StringValue value) implements Literal<StringValue> {
	@Override
	public void represent(StringRepresentation representation) {
		representation.append(StringEscapeUtils.quote(value.value, false));
	}
}
