package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.FunctionConstructor;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.Arrays;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;

public final class FunctionPrototype extends Executable {
	public static final FunctionPrototype instance = new FunctionPrototype();

	static {
		instance.putMethod(Names.call, FunctionPrototype::callMethod);
		instance.putMethod(Names.toString, FunctionPrototype::toStringMethod);
		instance.put(Names.constructor, FunctionConstructor.instance);
	}

	private FunctionPrototype() {
		super(Names.EMPTY);
	}

	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		return Executable.getExecutable(interpreter.thisValue()).toStringMethod();
	}

	private static Value<?> callMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Executable func = Executable.getExecutable(interpreter.thisValue());
		final Value<?> thisArg = argument(0, arguments);
		final Value<?>[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
		return func.call(interpreter, thisArg, args);
	}

	@Override
	public StringValue toStringMethod() {
		return NativeFunction.toStringForName("");
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) {
		return Undefined.instance;
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-function-prototype-object")
	public ObjectPrototype getDefaultPrototype() {
		return ObjectPrototype.instance;
	}
}