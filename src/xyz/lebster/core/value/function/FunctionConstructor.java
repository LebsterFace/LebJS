package xyz.lebster.core.value.function;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class FunctionConstructor extends BuiltinConstructor<Function, FunctionPrototype> {
	public FunctionConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Function, 1);
	}

	public Function construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) {
		throw new NotImplemented("new Function()");
	}

	@Override
	public Function internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		throw new NotImplemented("Function()");
	}
}
