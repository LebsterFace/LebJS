package xyz.lebster.core.node;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;


public class ReturnStatement implements Statement {
	public final Expression argument;

	public ReturnStatement(Expression argument) {
		this.argument = argument;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "ReturnStatement");
		argument.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw new AbruptCompletion(AbruptCompletion.Type.Return, argument.execute(interpreter));
	}
}
