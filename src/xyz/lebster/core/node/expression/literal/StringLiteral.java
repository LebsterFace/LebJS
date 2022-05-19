package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.string.StringValue;

public record StringLiteral(StringValue value) implements Literal<StringValue> {
	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "String", value.value);
	}

	@Override
	public void represent(StringRepresentation representation) {
		final char quoteType = this.value.value.contains("'") ? '"' : '\'';
		representation.append(quoteType);
		representation.append(this.value.value);
		representation.append(quoteType);
	}
}
