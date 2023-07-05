package xyz.lebster.core.value.error.runtimesyntax;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public final class SyntaxErrorConstructor extends BuiltinConstructor<SyntaxErrorObject, SyntaxErrorPrototype> {
	public SyntaxErrorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.SyntaxError, 1);
	}

	@Override
	public SyntaxErrorObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new SyntaxErrorObject(interpreter, argumentString(0, "", interpreter, arguments));
	}
}
