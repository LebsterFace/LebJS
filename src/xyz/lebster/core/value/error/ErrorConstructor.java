package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

// TODO: NativeError https://tc39.es/ecma262/multipage#sec-nativeerror-constructors
public final class ErrorConstructor extends BuiltinConstructor<ErrorObject, ErrorPrototype> {
	public ErrorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Error, 1);
	}

	@Override
	public ErrorObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new ErrorObject(interpreter, interpreter.intrinsics.errorPrototype, argumentString(0, "", interpreter, arguments));
	}
}
