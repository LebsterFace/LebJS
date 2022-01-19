package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

public final class FunctionPrototype extends Executable<Void> {
	public static final FunctionPrototype instance = new FunctionPrototype();

	private FunctionPrototype() {
		super(null);
	}

	@Override
	protected String getName() {
		return "FunctionPrototype";
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) {
		return UndefinedValue.instance;
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-function-prototype-object")
	public ObjectPrototype getDefaultPrototype() {
		return ObjectPrototype.instance;
	}
}