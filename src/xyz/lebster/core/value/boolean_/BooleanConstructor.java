package xyz.lebster.core.value.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor")
public class BooleanConstructor extends BuiltinConstructor<BooleanWrapper, BooleanPrototype> {
	public BooleanConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Boolean);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor-boolean-value")
	public BooleanWrapper construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		// 20.3.1.1 Boolean ( value )
		final Value<?> value = argument(0, arguments);

		// 1. Let b be ToBoolean(value).
		final BooleanValue b = value.toBooleanValue(interpreter);
		// FIXME: 3. Let O be ? OrdinaryCreateFromConstructor(NewTarget, "%Boolean.prototype%", « [[BooleanData]] »).
		// 4. Set O.[[BooleanData]] to b.
		// 5. Return O.
		return new BooleanWrapper(interpreter.intrinsics.booleanPrototype, b);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor-boolean-value")
	public BooleanValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.3.1.1 Boolean ( value )
		final Value<?> value = argument(0, arguments);

		// 1. Let b be ToBoolean(value).
		// 2. If NewTarget is undefined, return b.
		return value.toBooleanValue(interpreter);
	}
}
