package xyz.lebster.core.expression;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.BlockStatement;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Function;
import xyz.lebster.core.value.Value;


public class FunctionExpression extends FunctionNode implements Expression {
	public FunctionExpression(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	public FunctionExpression(Identifier name, Identifier... arguments) {
		super(name, arguments);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "FunctionExpression", getCallString());
		for (final ASTNode child : body.children) {
			child.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Function(this);
	}
}
