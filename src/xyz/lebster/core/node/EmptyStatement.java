package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public class EmptyStatement implements ASTNode {
	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("EmptyStatement");
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
//		FIXME: Could cause issues?
		return null;
	}
}
