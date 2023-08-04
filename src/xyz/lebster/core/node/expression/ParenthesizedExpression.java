package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

public record ParenthesizedExpression(Expression expression, SourceRange range) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}
}