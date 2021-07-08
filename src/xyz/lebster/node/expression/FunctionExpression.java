package xyz.lebster.node.expression;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.BlockStatement;
import xyz.lebster.node.FunctionNode;
import xyz.lebster.node.value.Function;
import xyz.lebster.node.value.Value;

public class FunctionExpression extends FunctionNode implements Expression {
	public FunctionExpression(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return new Function(this);
	}
}
