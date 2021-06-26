package xyz.lebster.core.node;

import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Function;

public class FunctionDeclaration extends FunctionNode implements Declaration, Statement {
	public FunctionDeclaration(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	public FunctionDeclaration(Identifier name, Identifier... arguments) {
		super(name, arguments);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "FunctionDeclaration", getCallString());
		for (final ASTNode child : body.children) {
			child.dump(indent + 1);
		}
	}

	@Override
	public Function execute(Interpreter interpreter) {
		final Function value = new Function(this);
		interpreter.declareVariable(name, value);
		return value;
	}
}
