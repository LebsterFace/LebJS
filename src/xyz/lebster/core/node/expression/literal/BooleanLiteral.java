package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.BooleanValue;

public record BooleanLiteral(BooleanValue booleanValue) implements Literal<BooleanValue> {
	@Override
	public BooleanValue execute(Interpreter interpreter) {
		return this.booleanValue;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, booleanValue.type.name(), booleanValue == BooleanValue.TRUE ? "true" : "false");
	}
}
