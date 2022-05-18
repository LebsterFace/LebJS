package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.PrimitiveValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.BuiltinPrototype;

public abstract class PrimitiveWrapper<T extends PrimitiveValue<?>, P extends BuiltinPrototype<?, ?>> extends ObjectValue {
	public final T data;

	public PrimitiveWrapper(P prototype, T data) {
		super(prototype);
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
