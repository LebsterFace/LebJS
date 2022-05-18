package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ArrayObject;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;
import static xyz.lebster.core.runtime.value.native_.NativeFunction.argumentInt;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-constructor")
public class ArrayConstructor extends BuiltinConstructor<ArrayObject, ArrayPrototype> {
	public ArrayConstructor(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype, fp, Names.Array);
		this.putMethod(fp, Names.of, ArrayConstructor::of);
	}

	@NonStandard
	private static Value<?> of(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final int len = argumentInt(0, 0, interpreter, arguments);
		final Value<?> callbackFn = argument(1, arguments);
		final Value<?> thisArg = argument(2, arguments);

		final Executable executable = Executable.getExecutable(interpreter, callbackFn);
		final Value<?>[] result = new Value<?>[len];
		for (int k = 0; k < len; k++)
			result[k] = executable.call(interpreter, thisArg, new NumberValue(k));
		return new ArrayObject(interpreter, result);
	}

	@Override
	public ArrayObject construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Array()");
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("Array()");
	}
}
