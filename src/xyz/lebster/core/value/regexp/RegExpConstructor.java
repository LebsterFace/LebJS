package xyz.lebster.core.value.regexp;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public class RegExpConstructor extends BuiltinConstructor<RegExpObject, RegExpPrototype> {
	public RegExpConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.RegExp, 2);
	}

	@Override
	public RegExpObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		final String pattern = argumentString(0, "(?:)", interpreter, arguments);
		final String flags = argumentString(1, "", interpreter, arguments);
		return new RegExpObject(interpreter, pattern, flags);
	}
}
