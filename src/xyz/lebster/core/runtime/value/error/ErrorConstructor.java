package xyz.lebster.core.runtime.value.error;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.BuiltinConstructor;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argumentString;

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
