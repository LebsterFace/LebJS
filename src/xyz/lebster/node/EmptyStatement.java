package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;

public final class EmptyStatement implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Undefined();
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "EmptyStatement");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(';');
	}
}