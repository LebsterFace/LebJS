package xyz.lebster.core.node;

import xyz.lebster.core.Interpreter;

public enum BinaryOp {
	Add,
	Subtract,
	Divide,
	Multiply;

	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("BinaryOp: ");
		System.out.println(this);

//		System.out.println(switch (this) {
//			case Add -> "+";
//			case Divide -> "/";
//			case Multiply -> "*";
//			case Subtract -> "-";
//		});
	}
}