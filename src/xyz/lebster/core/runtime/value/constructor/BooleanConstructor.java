package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.BooleanWrapper;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.prototype.BooleanPrototype;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor")
public class BooleanConstructor extends BuiltinConstructor<BooleanWrapper, BooleanPrototype> {
	public BooleanConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Boolean);
	}

	public BooleanWrapper construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Boolean()");
	}

	@Override
	public BooleanValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		return argument(0, arguments).toBooleanValue(interpreter);
	}
}
