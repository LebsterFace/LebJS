package xyz.lebster.core.value.boolean_;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.PrimitiveConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.FunctionPrototype;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor")
@NonStandard
public class BooleanConstructor extends PrimitiveConstructor {
	public BooleanConstructor(FunctionPrototype functionPrototype) {
		super(functionPrototype, Names.Boolean);
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
