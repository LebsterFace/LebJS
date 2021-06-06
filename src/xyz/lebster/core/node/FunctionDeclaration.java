package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Function;

public class FunctionDeclaration extends ScopeNode {
	public final Identifier name;

	public FunctionDeclaration(Identifier name) {
		this.name = name;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("FunctionDeclaration ");
		System.out.print(name);
		System.out.println(":");
		for (ASTNode child : children) child.dump(indent + 1);
	}

	@Override
	public Function execute(Interpreter interpreter) {
		final Function value = new Function(this);
		interpreter.declareVariable(name, value);
		return value;
	}

	public CallExpression getCall() {
		return new CallExpression(name);
	}
}