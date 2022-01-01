package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.declaration.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.node.value.Function;
import xyz.lebster.core.node.value.Value;

public final class FunctionExpression extends FunctionNode implements Expression {
	public FunctionExpression(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Function(this, interpreter.getExecutionContext());
	}
}