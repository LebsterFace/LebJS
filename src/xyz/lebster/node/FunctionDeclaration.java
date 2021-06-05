package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.value.Function;

public class FunctionDeclaration extends ScopeNode {
	public final String name;

	public FunctionDeclaration(String name) {
		this.name = name;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("FunctionDeclaration '");
		System.out.print(this.name);
		System.out.println("':");
		for (ASTNode child : children) child.dump(indent + 1);
	}

	@Override
	public Function execute(Interpreter interpreter) {
		final Function value = new Function(this);
		interpreter.setGlobal(name, value);
		return value;
	}

	public CallExpression getCall() {
		return new CallExpression(name);
	}
}