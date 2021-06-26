package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;

public enum AssignmentOp {
	Equals;

	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("AssignmentOp: ");
		System.out.println(this);
	}
}
