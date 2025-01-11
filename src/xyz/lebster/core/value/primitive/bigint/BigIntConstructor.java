package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonCompliant
public class BigIntConstructor extends PrimitiveConstructor {
	public BigIntConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.BigInt);
		// TODO: Static methods
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bigint-constructor-number-value")
	public Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 21.2.1.1 BigInt ( value )
		final Value<?> value = argument(0, arguments);

		// 1. If NewTarget is not undefined, throw a TypeError exception.
		// 2. Let prim be ? ToPrimitive(value, number).
		final PrimitiveValue<?> prim = value.toPrimitive(interpreter, PreferredType.Number);
		// 3. If prim is a Number, return ? NumberToBigInt(prim).
		if (prim instanceof final NumberValue N) return N.numberToBigInt(interpreter);
		// 4. Otherwise, return ? ToBigInt(prim).
		return prim.toBigIntValue(interpreter);
	}
}
