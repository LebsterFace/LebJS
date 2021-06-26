package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;

public enum BinaryOp {
	Add, Subtract, Divide, Multiply;

	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("BinaryOp: ");
		System.out.println(this);
	}
}
