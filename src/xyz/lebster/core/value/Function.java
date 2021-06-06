package xyz.lebster.core.value;

import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.runtime.Interpreter;

public class Function extends Value<ScopeNode> {
	public Function(ScopeNode value) {
		super(Type.Function, value);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("FunctionObject");
	}
}
