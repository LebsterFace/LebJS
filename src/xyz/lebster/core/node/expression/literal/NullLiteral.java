package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Null;

public final class NullLiteral implements Literal<Null> {
	@Override
	public Null execute(Interpreter interpreter) {
		return Null.instance;
	}
}
