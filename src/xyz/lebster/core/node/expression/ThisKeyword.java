package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Value;

public final class ThisKeyword implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.thisValue();
	}

	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", "this");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("this");
	}
}