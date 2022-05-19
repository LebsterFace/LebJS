package xyz.lebster.core.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.number.NumberValue;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

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
