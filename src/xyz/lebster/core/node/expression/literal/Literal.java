package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Value;

public interface Literal<ValueType extends Value<?>> extends Expression {
	ValueType execute(Interpreter interpreter);

	@Override
	default void dump(int indent) {
		throw new NotImplemented("LiteralExpression#dump");
	}
}
