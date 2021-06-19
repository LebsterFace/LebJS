package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;

public enum AssignmentOp {
	Equals;

	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("AssignmentOp: ");
		System.out.println(this);

//		System.out.println(switch (this) {
//			case Add -> "+";
//			case Divide -> "/";
//			case Multiply -> "*";
//			case Subtract -> "-";
//		});
	}
}
