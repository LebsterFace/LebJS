package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;

public class ElseStatement extends ScopeNode {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "ElseStatement");
		for (final ASTNode child : children) {
			child.dump(indent + 1);
		}
	}
}
