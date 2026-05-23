package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.NumberToJSON;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
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
import java.math.MathContext;
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

		final NumberValue numberValue_ = thisNumberValue(interpreter);
		if (precision == Undefined.instance) {
			return numberValue_.toStringValue(interpreter);
		}

		final double numberValue = numberValue_.value;
		if (!Double.isFinite(numberValue)) {
			return numberValue_.toStringValue(interpreter);
		}

		final int precisionCount = toIntegerOrInfinity(interpreter, precision);
		if (precisionCount < 1 || precisionCount > 100) {
			throw error(new RangeError(interpreter, "toPrecision() argument must be between 1 and 100"));
		}

		if (numberValue == 0) {
			if (precisionCount == 1) {
				return new StringValue("0");
			}

			return new StringValue("0." + "0".repeat(precisionCount - 1));
		}

		final MathContext mc = new MathContext(precisionCount, RoundingMode.HALF_UP);
		final BigDecimal d = new BigDecimal(numberValue).round(mc);
		final int exponent = d.precision() - d.scale() - 1;

		// Exponential form
		if (exponent < -6 || exponent >= precisionCount) {
			final BigDecimal significand = d.movePointLeft(exponent).setScale(precisionCount - 1, RoundingMode.UNNECESSARY);
			final String exponentString = exponent >= 0 ? "+" + exponent : Integer.toString(exponent);
			return new StringValue(significand.toPlainString() + "e" + exponentString);
		}

		// Fixed form
		final int scale = Math.max(0, precisionCount - exponent - 1);
		final BigDecimal fixed = d.setScale(scale, RoundingMode.UNNECESSARY);
		return new StringValue(fixed.toPlainString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.toexponential")
	private static StringValue toExponential(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.2 Number.prototype.toExponential ( fractionDigits )
		final Value<?> fractionDigits = argument(0, arguments);

		final NumberValue numberValue_ = thisNumberValue(interpreter);
		final double numberValue = numberValue_.value;
		if (!Double.isFinite(numberValue)) {
			return numberValue_.toStringValue(interpreter);
		}

		final int fractionCount = toIntegerOrInfinity(interpreter, fractionDigits);
		if (fractionCount < 0 || fractionCount > 100) {
			throw error(new RangeError(interpreter, "toExponential() argument must be between 0 and 100"));
		}

		if (fractionDigits == Undefined.instance) {
			return new StringValue(NumberToJSON.serializeNumber(numberValue, true));
		}

		final MathContext mc = new MathContext(fractionCount + 1, RoundingMode.HALF_UP);
		final BigDecimal d = new BigDecimal(numberValue).round(mc);
		final int exponent = d.precision() - d.scale() - 1;
		final BigDecimal significand = d.movePointLeft(exponent).setScale(fractionCount, RoundingMode.UNNECESSARY);
		final String exponentString = exponent >= 0 ? "+" + exponent : Integer.toString(exponent);
		return new StringValue(significand.toPlainString() + "e" + exponentString);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tofixed")
	private static StringValue toFixed(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 21.1.3.3 Number.prototype.toFixed ( fractionDigits )
		final Value<?> fractionDigits = argument(0, arguments);

		final NumberValue numberValue_ = thisNumberValue(interpreter);
		final int fractionCount = toIntegerOrInfinity(interpreter, fractionDigits);

		if (fractionCount < 0 || fractionCount > 100) {
			throw error(new RangeError(interpreter, "toFixed() argument must be between 0 and 100"));
		}

		double numberValue = numberValue_.value;
		if (!Double.isFinite(numberValue)) {
			return numberValue_.toStringValue(interpreter);
		}

		if (Math.abs(numberValue) >= 1e21) {
			return numberValue_.toStringValue(interpreter);
		}

		final String sign = numberValue < 0 ? "-" : "";
		numberValue = Math.abs(numberValue);
		final BigDecimal d = new BigDecimal(numberValue).setScale(fractionCount, RoundingMode.HALF_UP);
		return new StringValue(sign + d.toPlainString());
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