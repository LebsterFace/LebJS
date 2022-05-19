package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

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