package xyz.lebster.core.node.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.BooleanValue;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.Primitive;
import xyz.lebster.core.node.value.StringValue;

public abstract class PrimitiveWrapper<P extends Primitive<?>> extends ObjectValue {
	public final P data;

	public PrimitiveWrapper(P data) {
		this.data = data;
	}

	@Override
	public StringValue toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toStringLiteral(interpreter);
	}

	@Override
	public NumberValue toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toNumericLiteral(interpreter);
	}

	@Override
	public BooleanValue toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion {
		return data.toBooleanLiteral(interpreter);
	}
}
