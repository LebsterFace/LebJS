package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public class EmptyStatement implements Statement {
	@Override
	public void dump(int indent) {
		Interpreter.dumpSingle(indent, "EmptyStatement");
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
//		FIXME: Could cause issues?
		return null;
	}
}
