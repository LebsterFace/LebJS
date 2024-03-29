package xyz.lebster.core.value.primitive.boolean_;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor")
@NonStandard
public class BooleanConstructor extends PrimitiveConstructor {
	public BooleanConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Boolean);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-constructor-boolean-value")
	public BooleanValue internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.3.1.1 Boolean ( value )
		final Value<?> value = argument(0, arguments);

		// 1. Let b be ToBoolean(value).
		// 2. If NewTarget is undefined, return b.
		return value.toBooleanValue(interpreter);
	}
}
