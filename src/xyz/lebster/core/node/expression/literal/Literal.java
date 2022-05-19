package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;

public interface Literal<ValueType extends Value<?>> extends Expression {
	default ValueType execute(Interpreter interpreter) {
		return this.value();
	}

	ValueType value();
}
