package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.Value;

public final class EmptyStatement implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return UndefinedValue.instance;
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