package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;

public class EmptyStatement implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Undefined();
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "EmptyStatement");
	}
}