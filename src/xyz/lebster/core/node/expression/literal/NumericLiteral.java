package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.NumberValue;

public record NumericLiteral(NumberValue numberValue) implements Literal<NumberValue> {
	@Override
	public NumberValue execute(Interpreter interpreter) {
		return this.numberValue;
	}
}
