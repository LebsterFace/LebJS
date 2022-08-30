package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public final class ReferenceErrorConstructor extends BuiltinConstructor<ReferenceError, ReferenceErrorPrototype> {
	public ReferenceErrorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.ReferenceError);
	}

	@Override
	public ReferenceError construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new ReferenceError(interpreter, argumentString(0, "", interpreter, arguments));
	}

	@Override
	public ReferenceError call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return this.construct(interpreter, arguments, this);
	}
}
