package xyz.lebster.core.node;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;


public record ThrowStatement(Expression expression) implements Statement {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "ThrowStatement");
		expression.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		interpreter.setCompletion(new AbruptCompletion(AbruptCompletion.Type.Throw, expression.execute(interpreter)));
		return null;
	}
}
