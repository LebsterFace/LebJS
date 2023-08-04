package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.primitive.PrimitiveValue;

public record PrimitiveLiteral<V extends PrimitiveValue<?>>(SourceRange range, V value) implements Expression {
	@Override
	public V execute(Interpreter interpreter) {
		return this.value;
	}
}
