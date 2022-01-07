package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

public abstract class PrimitiveWrapper<P extends Primitive<?>> extends ObjectLiteral {
	public final P data;

	public PrimitiveWrapper(P data) {
		this.data = data;
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toStringLiteral(interpreter);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toNumericLiteral(interpreter);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toBooleanLiteral(interpreter);
	}
}
