package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.Arrays;

public final class FunctionPrototype extends Executable<Void> {
	public static final FunctionPrototype instance = new FunctionPrototype();

	static {
		// FIXME: FunctionConstructor
		instance.putMethod("call", (interpreter, arguments) -> {
			final Executable<?> func = Executable.getExecutable(interpreter.thisValue());
			final Value<?> thisArg = arguments.length > 0 ? arguments[0] : Undefined.instance;
			final Value<?>[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
			return func.call(interpreter, thisArg, args);
		});
	}

	private FunctionPrototype() {
		super(null);
	}

	@Override
	protected String getName() {
		return "FunctionPrototype";
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