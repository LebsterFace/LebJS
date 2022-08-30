package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public final class RangeErrorConstructor extends BuiltinConstructor<RangeError, RangeErrorPrototype> {
	public RangeErrorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.RangeError);
	}

	@Override
	public RangeError construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new RangeError(interpreter, argumentString(0, "", interpreter, arguments));
	}

	@Override
	public RangeError call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return this.construct(interpreter, arguments, this);
	}
}
