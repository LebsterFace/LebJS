package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayPrototype;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-number-prototype-object")
public final class NumberPrototype extends ObjectValue {
	private static final double TEN_TO_THE_TWENTY_ONE = 1000000000000000000000.0D;

	public NumberPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.toString, 0, NumberPrototype::toStringMethod);
		putMethod(intrinsics, Names.toFixed, 1, NumberPrototype::toFixed);
		putMethod(intrinsics, Names.valueOf, 0, NumberPrototype::valueOf);
		putMethod(intrinsics, Names.toLocaleString, 0, NumberPrototype::toLocaleString);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tofixed")
	private static StringValue toFixed(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.3 Number.prototype.toFixed ( fractionDigits )
		final Value<?> fractionDigits = argument(0, arguments);

		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x_ = interpreter.thisValue().toNumberValue(interpreter);
		// 2. Let f be ? ToIntegerOrInfinity(fractionDigits).
		final int f = toIntegerOrInfinity(interpreter, fractionDigits);
		// 3. Assert: If fractionDigits is undefined, then f is 0.
		if (fractionDigits == Undefined.instance && f != 0) throw new ShouldNotHappen("Assertion failed");
		// 4. If f is not finite, throw a RangeError exception.
		// 5. If f < 0 or f > 100, throw a RangeError exception.
		if (f < 0 || f > 100) throw error(new RangeError(interpreter, "toFixed() digits argument must be between 0 and 100"));
		// 6. If x is not finite, return Number::toString(x, 10).
		if (x_.value.isInfinite()) return x_.toStringValue(interpreter);
		// 7. Set x to ℝ(x).
		double x = x_.value;
		// 8. Let s be the empty String.
		String s = "";
		// 9. If x < 0, then
		if (x < 0) {
			// a. Set s to "-".
			s = "-";
			// b. Set x to -x.
			x = -x;
		}

		// 10. If x ≥ 10^21, then a. Let m be ! ToString(𝔽(x)).
		// 11. Else, (String.format used instead)
		final String m = x >= TEN_TO_THE_TWENTY_ONE ? x_.toStringValue(interpreter).value : String.format("%." + f + "f", x);

		// 12. Return the string-concatenation of s and m.
		return new StringValue(s + m);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.valueof")
	private static NumberValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.7 Number.prototype.valueOf ( )

		// 1. Return ? thisNumberValue(this value).
		return thisNumberValue(interpreter, interpreter.thisValue());
	}

	private static NumberValue thisNumberValue(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		// 1. If Type(value) is Number, return value.
		if (value instanceof final NumberValue numberValue) return numberValue;
		// 2. If Type(value) is Object and value has a [[NumberData]] internal slot, then
		if (value instanceof final NumberWrapper numberWrapper) {
			// a. Let n be value.[[NumberData]].
			// b. Assert: Type(n) is Number.
			// c. Return n.
			return numberWrapper.data;
		}
		// 3. Throw a TypeError exception.
		throw error(new TypeError(interpreter, "This method requires that 'this' be a Number"));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tointegerorinfinity")
	public static int toIntegerOrInfinity(Interpreter interpreter, Value<?> argument) throws AbruptCompletion {
		// 1. Let number be ? ToNumber(argument).
		final NumberValue numberValue = argument.toNumberValue(interpreter);
		// 2. If number is NaN, +0𝔽, or -0𝔽, return 0.
		if (numberValue.value.isNaN() || numberValue.value == 0) return 0;
		// 3. If number is +∞𝔽, return +∞.
		// 4. If number is -∞𝔽, return -∞.
		if (numberValue.value.isInfinite()) {
			if (numberValue.value > 0) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
		// 5. Let integer be floor(abs(ℝ(number))).
		long integer = (long) Math.floor(Math.abs(numberValue.value));
		// 6. If number < +0𝔽, set integer to -integer.
		if (numberValue.value < 0) integer = -integer;
		// 7. Return integer.
		if (integer > Integer.MAX_VALUE)
			throw new NotImplemented("ToIntegerOrInfinity returning values over 2^31-1");

		return (int) integer;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tolength")
	public static int toLength(Interpreter interpreter, Value<?> argument) throws AbruptCompletion {
		// 1. Let len be ? ToIntegerOrInfinity(argument).
		final int len = toIntegerOrInfinity(interpreter, argument);
		// 2. If len ≤ 0, return +0𝔽.
		if (len <= 0) return 0;
		// 3. Return 𝔽(min(len, 2^53 - 1)).
		return Math.toIntExact(Math.min(len, ArrayPrototype.MAX_LENGTH));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tolocalestring")
	@NonCompliant
	private static StringValue toLocaleString(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 21.1.3.4 Number.prototype.toLocaleString ( [ reserved1 [ , reserved2 ] ] )

		throw new NotImplemented("Number.prototype.toLocaleString");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tostring")
	@NonCompliant
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.6 Number.prototype.toString ( [ radix ] )
		final Value<?> radix = argument(0, arguments);

		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x = thisNumberValue(interpreter, interpreter.thisValue());
		// 2. If radix is undefined, let radixMV be 10.
		// 3. Else, let radixMV be ? ToIntegerOrInfinity(radix).
		final int radixMV = radix == Undefined.instance ? 10 : toIntegerOrInfinity(interpreter, radix);
		// 4. If radixMV < 2 or radixMV > 36, throw a RangeError exception.
		if (radixMV < 2 || radixMV > 36)
			throw error(new TypeError(interpreter, "toString() radix argument must be between 2 and 36"));
		// 5. If radixMV = 10, return ! ToString(x).
		if (radixMV == 10) return x.toStringValue(interpreter);
		// 6. Return the String representation of this Number value using the radix specified by radixMV. Letters a-z
		// are used for digits with values 10 through 35. The precise algorithm is implementation-defined, however the
		// algorithm should be a generalization of that specified in 6.1.6.1.20.
		// FIXME: Follow spec
		return x.toStringValue(interpreter);
	}
}