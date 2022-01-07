package xyz.lebster.core.node.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.BooleanLiteral;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.Primitive;
import xyz.lebster.core.node.value.StringLiteral;

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
