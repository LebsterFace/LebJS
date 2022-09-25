package xyz.lebster.core.value.error.type;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public final class TypeErrorConstructor extends BuiltinConstructor<TypeError, TypeErrorPrototype> {
	public TypeErrorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.TypeError);
	}

	@Override
	public TypeError construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new TypeError(interpreter, argumentString(0, "", interpreter, arguments));
	}
}
