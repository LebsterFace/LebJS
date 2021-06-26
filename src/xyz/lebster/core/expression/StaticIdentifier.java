package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;

public abstract class StaticIdentifier extends Expression {
	public abstract String getName();

	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("Identifier: '");
		System.out.print(getName());
		System.out.println("'");
	}
}
