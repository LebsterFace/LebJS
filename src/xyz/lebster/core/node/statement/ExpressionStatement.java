package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;

public record ExpressionStatement(Expression expression) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}

	@Override
	public SourceRange range() {
		return expression.range();
	}
}