package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.Executable;

public class FunctionPrototype extends Executable<Void> {
	public static final FunctionPrototype instance = new FunctionPrototype();

	private FunctionPrototype() {
		super(null);
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