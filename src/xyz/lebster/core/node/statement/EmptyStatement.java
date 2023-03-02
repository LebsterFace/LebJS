package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
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
		DumpBuilder.begin(indent).selfString(this);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(';');
	}
}