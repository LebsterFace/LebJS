package xyz.lebster.core.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public abstract class BuiltinConstructor<T extends ObjectValue, P extends ObjectValue> extends Constructor {
	public BuiltinConstructor(Intrinsics intrinsics, StringValue name) {
		super(intrinsics, name);
	}

	@Override
	public final StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}

	@Override
	public abstract T construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion;
}
