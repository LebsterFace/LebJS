package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.StringValue;

public record StringLiteral(StringValue stringValue) implements Literal<StringValue> {
	@Override
	public StringValue execute(Interpreter interpreter) {
		return this.stringValue;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, stringValue.type.name(), stringValue.value);
	}
}
