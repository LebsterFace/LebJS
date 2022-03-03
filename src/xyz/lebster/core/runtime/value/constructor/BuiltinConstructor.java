package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Constructor;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

abstract class BuiltinConstructor<T extends ObjectValue> extends Constructor {
	public BuiltinConstructor(StringValue name) {
		super(name);
	}

	@Override
	public final StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}

	@Override
	public abstract T construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
