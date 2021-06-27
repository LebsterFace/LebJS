package xyz.lebster.core.expression;

import xyz.lebster.core.node.Statement;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;


public record ExpressionStatement(Expression expression) implements Statement {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "ExpressionStatement");
		expression.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}
}
