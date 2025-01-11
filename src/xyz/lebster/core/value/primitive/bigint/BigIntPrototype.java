package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;

public class BigIntPrototype extends ObjectValue {
	public BigIntPrototype(Intrinsics intrinsics) {
		super(intrinsics.bigIntPrototype);

		putMethod(intrinsics, Names.toString, 1, BigIntPrototype::toStringMethod);
		putMethod(intrinsics, Names.valueOf, 0, BigIntPrototype::valueOf);
		put(SymbolValue.toStringTag, Names.BigInt, false, false, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bigint.prototype.valueof")
	private static BigIntValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.2.3.4 BigInt.prototype.valueOf ( )

		// 1. Return ? ThisBigIntValue(this value).
		return thisBigIntValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bigint.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.2.3.3 BigInt.prototype.toString ( [ radix ] )
		final Value<?> radix = argument(0, arguments);

		// 1. Let x be ? ThisBigIntValue(this value).
		final BigIntValue x = thisBigIntValue(interpreter);
		// 2. If radix is undefined, let radixMV be 10.
		// 3. Else, let radixMV be ? ToIntegerOrInfinity(radix).
		final int radixMV = radix == Undefined.instance ? 10 : toIntegerOrInfinity(interpreter, radix);
		// 4. If radixMV is not in the inclusive interval from 2 to 36, throw a RangeError exception.
		if (radixMV < 2 || radixMV > 36)
			throw error(new RangeError(interpreter, "toString() radix argument must be between 2 and 36"));
		// 5. Return BigInt::toString(x, radixMV).
		return new StringValue(x.value.toString(radixMV));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-thisbigintvalue")
	private static BigIntValue thisBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();

		// 1. If value is a BigInt, return value.
		if (value instanceof final BigIntValue result) return result;
		// 2. If value is an Object and value has a [[BigIntData]] internal slot, then
		if (value instanceof final BigIntWrapper wrapper) {
			// a. Assert: value.[[BigIntData]] is a BigInt.
			// b. Return value.[[BigIntData]].
			return wrapper.data;
		}

		// 3. Throw a TypeError exception.
		throw error(interpreter.incompatibleReceiver("BigInt.prototype", "a BigInt"));
	}
}
