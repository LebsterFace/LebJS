package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
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
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-number-prototype-object")
public final class NumberPrototype extends ObjectValue {
	public NumberPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.toExponential, 1, NumberPrototype::toExponential);
		putMethod(intrinsics, Names.toFixed, 1, NumberPrototype::toFixed);
		putMethod(intrinsics, Names.toLocaleString, 0, NumberPrototype::toLocaleString);
		putMethod(intrinsics, Names.toPrecision, 1, NumberPrototype::toPrecision);
		putMethod(intrinsics, Names.toString, 1, NumberPrototype::toStringMethod);
		putMethod(intrinsics, Names.valueOf, 0, NumberPrototype::valueOf);

		// Non-standard
		putMethod(intrinsics, Names.toExactString, 0, NumberPrototype::toExactString);
	}

	@NonStandard
	private static StringValue toExactString(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final NumberValue x = thisNumberValue(interpreter);
		return new StringValue(x.toExactString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.toprecision")
	private static StringValue toPrecision(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.5 Number.prototype.toPrecision ( precision )
		final Value<?> precision = argument(0, arguments);

		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x_ = thisNumberValue(interpreter);
		// 2. If precision is undefined, return ! ToString(x).
		if (precision == Undefined.instance) return x_.toStringValue(interpreter);
		// 3. Let p be ? ToIntegerOrInfinity(precision).
		final int p = toIntegerOrInfinity(interpreter, precision);
		// 4. If x is not finite, return Number::toString(x, 10).
		if (!Double.isFinite(x_.value)) return x_.toStringValue(interpreter);
		// 5. If p < 1 or p > 100, throw a RangeError exception.
		if (p < 1 || p > 100) throw error(new RangeError(interpreter, "toPrecision() argument must be between 1 and 100"));
		// 6. Set x to ℝ(x).
		double x = x_.value;
		// 7. Let s be the empty String.
		String s = "";
		// 8. If x < 0, then
		if (x < 0) {
			// a. Set s to the code unit 0x002D (HYPHEN-MINUS).
			s = "-";
			// b. Set x to -x.
			x = -x;
		}

		String m;
		int e;
		// 9. If x = 0, then
		if (x == 0) {
			// a. Let m be the String value consisting of p occurrences of the code unit 0x0030 (DIGIT ZERO).
			m = "0".repeat(p);
			// b. Let e be 0.
			e = 0;
		}
		// 10. Else,
		else {
			// a. Let e and n be integers such that 10^(p - 1) ≤ n < 10^p and for which n × 10^(e - p + 1) - x is as close to zero as possible.
			// If there are two such sets of e and n, pick the e and n for which n × 10^(e - p + 1) is larger.
			e = (int) Math.floor(Math.log10(x));
			final BigInteger n = new BigDecimal(x).movePointLeft(e - p + 1).setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();

			// b. Let m be the String value consisting of the digits of the decimal representation of n (in order, with no leading zeroes).
			m = n.toString();
			// c. If e < -6 or e ≥ p, then
			if (e < -6 || e >= p) {
				// i. Assert: e ≠ 0.
				// ii. If p ≠ 1, then
				if (p != 1) {
					// 1. Let a be the first code unit of m.
					final char a = m.charAt(0);
					// 2. Let b be the other p - 1 code units of m.
					final String b = m.substring(1);
					// 3. Set m to the string-concatenation of a, ".", and b.
					m = a + "." + b;
				}
				// iii. If e > 0, then
				final char c;
				if (e > 0) {
					// 1. Let c be the code unit 0x002B (PLUS SIGN).
					c = '+';
				}
				// iv. Else,
				else {
					// 1. Assert: e < 0.
					// 2. Let c be the code unit 0x002D (HYPHEN-MINUS).
					c = '-';
					// 3. Set e to -e.
					e = -e;
				}

				// v. Let d be the String value consisting of the digits of the decimal representation of e (in order, with no leading zeroes).
				final String d = Long.toString(e);
				// vi. Return the string-concatenation of s, m, the code unit 0x0065 (LATIN SMALL LETTER E), c, and d.
				return new StringValue(s + m + 'e' + c + d);
			}
		}

		// 11. If e = p - 1, return the string-concatenation of s and m.
		if (e == p - 1) return new StringValue(s + m);
		// 12. If e ≥ 0, then
		if (e >= 0) {
			// a. Set m to the string-concatenation of
			// the first e + 1 code units of m,
			// the code unit 0x002E (FULL STOP),
			// and the remaining p - (e + 1) code units of m.
			m = m.substring(0, e + 1) + '.' + m.substring(e + 1);
		}
		// 13. Else,
		else {
			// a. Set m to the string-concatenation of
			// the code unit 0x0030 (DIGIT ZERO),
			// the code unit 0x002E (FULL STOP),
			// -(e + 1) occurrences of the code unit 0x0030 (DIGIT ZERO),
			// and the String m.
			m = "0." + "0".repeat(-(e + 1)) + m;
		}

		// 14. Return the string-concatenation of s and m.
		return new StringValue(s + m);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.toexponential")
	private static StringValue toExponential(Interpreter interpreter, Value<?>[] arguments) {
		// 21.1.3.2 Number.prototype.toExponential ( fractionDigits )
		final Value<?> fractionDigits = argument(0, arguments);

		throw new NotImplemented("Number.prototype.toExponential");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tofixed")
	private static StringValue toFixed(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.3 Number.prototype.toFixed ( fractionDigits )
		final Value<?> fractionDigits = argument(0, arguments);

		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x_ = thisNumberValue(interpreter);
		// 2. Let f be ? ToIntegerOrInfinity(fractionDigits).
		final int f = toIntegerOrInfinity(interpreter, fractionDigits);
		// 3. Assert: If fractionDigits is undefined, then f is 0.
		if (fractionDigits == Undefined.instance && f != 0) throw new ShouldNotHappen("Assertion failed");
		// 4. If f is not finite, throw a RangeError exception.
		// 5. If f < 0 or f > 100, throw a RangeError exception.
		if (f < 0 || f > 100) throw error(new RangeError(interpreter, "toFixed() digits argument must be between 0 and 100"));
		// 6. If x is not finite, return Number::toString(x, 10).
		double x = x_.value;
		if (!Double.isFinite(x)) return x_.toStringValue(interpreter);
		// 7. Set x to ℝ(x).
		// 8. Let s be the empty String.
		String s = "";
		// 9. If x < 0, then
		if (x < 0) {
			// a. Set s to "-".
			s = "-";
			// b. Set x to -x.
			x = -x;
		}

		String m;
		// 10. If x ≥ 10^21, then
		if (x >= Math.pow(10, 21)) {
			// a. Let m be ! ToString(𝔽(x)).
			m = x_.stringValueOf(10);
		}
		// 11. Else,
		else {
			// a. Let n be an integer for which n / 10^f - x is as close to zero as possible.
			// If there are two such n, pick the larger n.
			final BigInteger n = new BigDecimal(x).movePointRight(f).setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();
			// b. If n = 0, let m be "0". Otherwise, let m be the String value consisting of the digits of the decimal representation of n (in order, with no leading zeroes).
			m = n.toString();
			// c. If f ≠ 0, then
			if (f != 0) {
				// i. Let k be the length of m.
				int k = m.length();
				// ii. If k ≤ f, then
				if (k <= f) {
					// 1. Let z be the String value consisting of f + 1 - k occurrences of the code unit 0x0030 (DIGIT ZERO).
					final String z = "0".repeat(f + 1 - k);
					// 2. Set m to the string-concatenation of z and m.
					m = z + m;
					// 3. Set k to f + 1.
					k = f + 1;
				}
				// iii. Let a be the first k - f code units of m.
				final String a = m.substring(0, k - f);
				// iv. Let b be the other f code units of m.
				final String b = m.substring(k - f);
				// v. Set m to the string-concatenation of a, ".", and b.
				m = a + "." + b;
			}
		}

		// 12. Return the string-concatenation of s and m.
		return new StringValue(s + m);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.valueof")
	private static NumberValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.7 Number.prototype.valueOf ( )

		// 1. Return ? thisNumberValue(this value).
		return thisNumberValue(interpreter);
	}

	private static NumberValue thisNumberValue(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();
		// 1. If Type(value) is Number, return value.
		if (value instanceof NumberValue numberValue) return numberValue;
		// 2. If Type(value) is Object and value has a [[NumberData]] internal slot, return n.
		if (value instanceof NumberWrapper numberWrapper) return numberWrapper.data;
		// 3. Throw a TypeError exception.
		throw error(interpreter.incompatibleReceiver("Number.prototype", "a Number"));
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
			throw new NotImplemented("ToIntegerOrInfinity returning values over 2^31-1 (%d)".formatted(integer));

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

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tolocalestring")
	private static StringValue toLocaleString(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 21.1.3.4 Number.prototype.toLocaleString ( [ reserved1 [ , reserved2 ] ] )

		// TODO: Implement `toLocaleString`
		return toStringMethod(interpreter, values);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.6 Number.prototype.toString ( [ radix ] )
		final Value<?> radix = argument(0, arguments);

		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x = thisNumberValue(interpreter);
		// 2. If radix is undefined, let radixMV be 10.
		// 3. Else, let radixMV be ? ToIntegerOrInfinity(radix).
		final int radixMV = radix == Undefined.instance ? 10 : toIntegerOrInfinity(interpreter, radix);
		// 4. If radixMV is not in the inclusive interval from 2 to 36, throw a RangeError exception.
		if (radixMV < 2 || radixMV > 36)
			throw error(new RangeError(interpreter, "toString() radix argument must be between 2 and 36"));

		// 5. Return Number::toString(x, radixMV).
		return new StringValue(x.stringValueOf(radixMV));
	}
}