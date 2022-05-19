package xyz.lebster.core.value.function;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.BuiltinPrototype;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.string.StringValue;

import java.util.Arrays;

import static xyz.lebster.core.value.function.NativeFunction.argument;

public final class FunctionPrototype extends BuiltinPrototype<Function, FunctionConstructor> {
	public FunctionPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
		this.putMethod(this, Names.call, FunctionPrototype::callMethod);
		this.putMethod(this, Names.toString, FunctionPrototype::toStringMethod);
	}

	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		return Executable.getExecutable(interpreter, interpreter.thisValue()).toStringMethod();
	}

	private static Value<?> callMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Executable func = Executable.getExecutable(interpreter, interpreter.thisValue());
		final Value<?> thisArg = argument(0, arguments);
		final Value<?>[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
		return func.call(interpreter, thisArg, args);
	}
}