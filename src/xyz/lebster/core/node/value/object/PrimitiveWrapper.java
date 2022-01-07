package xyz.lebster.core.node.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.BooleanValue;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.PrimitiveValue;
import xyz.lebster.core.node.value.StringValue;

public abstract class PrimitiveWrapper<P extends PrimitiveValue<?>> extends ObjectValue {
	public final P data;

	public PrimitiveWrapper(P data) {
		this.data = data;
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return data.toStringValue(interpreter);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		return data.toNumberValue(interpreter);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return data.toBooleanValue(interpreter);
	}
}
