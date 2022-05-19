package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

public final class ErrorConstructor extends BuiltinConstructor<ErrorObject, ErrorPrototype> {
	public ErrorConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Error);
	}

	@Override
	public ErrorObject construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final String message = argumentString(0, "", interpreter, arguments);
		return new ErrorObject(interpreter, message);
	}

	@Override
	public ErrorObject call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return this.construct(interpreter, arguments);
	}
}
