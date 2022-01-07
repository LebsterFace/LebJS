package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Value;

// TODO: Specialised classes for different Value types
public record LiteralExpression(Value<?> value) implements Expression {
	public Value<?> execute(Interpreter interpreter) {
		return this.value();
	}

	@Override
	public void dump(int indent) {
		throw new NotImplemented("LiteralExpression#dump");
	}
}
