package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ArrayObject;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-constructor")
public class ArrayConstructor extends BuiltinConstructor<ArrayObject> {
	public static final ArrayConstructor instance = new ArrayConstructor();

	static {
	}

	private ArrayConstructor() {
		super();
	}

	@Override
	public ArrayObject construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Array()");
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("Array()");
	}
}
