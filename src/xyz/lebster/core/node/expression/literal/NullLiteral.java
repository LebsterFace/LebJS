package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.NullValue;

public final class NullLiteral implements Literal<NullValue> {
	@Override
	public NullValue execute(Interpreter interpreter) {
		return NullValue.instance;
	}
}
