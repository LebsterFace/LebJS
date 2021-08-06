package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

public final class EmptyStatement implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return Undefined.instance;
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