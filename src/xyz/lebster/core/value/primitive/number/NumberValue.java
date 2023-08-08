package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.NumberToJSON;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigDecimal;
import java.math.BigInteger;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class NumberValue extends NumericValue<Double> {
	private static final String DIGITS = "0123456789abcdefghijklmnopqrstuvwxyz";

	public static final long TWO_TO_THE_31 = 2147483648L;
	public static final long TWO_TO_THE_32 = 4294967296L;
	public static final long UINT32_LIMIT = TWO_TO_THE_32 - 1;

	public static final long NEGATIVE_ZERO_BITS = 0x8000000000000000L;
	public static final long POSITIVE_ZERO_BITS = 0;
	public static final NumberValue EPSILON = new NumberValue(Math.ulp(1.0D));
	public static final NumberValue MAX_SAFE_INTEGER = new NumberValue(9007199254740991.0D);
	public static final NumberValue MAX_VALUE = new NumberValue(Double.MAX_VALUE);
	public static final NumberValue MINUS_ONE = new NumberValue(-1.0D);
	public static final NumberValue MIN_SAFE_INTEGER = new NumberValue(-9007199254740991.0D);
	public static final NumberValue MIN_VALUE = new NumberValue(Double.MIN_VALUE);
	public static final NumberValue NEGATIVE_INFINITY = new NumberValue(Double.NEGATIVE_INFINITY);
	public static final NumberValue NEGATIVE_ZERO = new NumberValue(-0.0D);
	public static final NumberValue NaN = new NumberValue(Double.NaN);
	public static final NumberValue ONE = new NumberValue(1.0D);
	public static final NumberValue POSITIVE_INFINITY = new NumberValue(Double.POSITIVE_INFINITY);
	public static final NumberValue ZERO = new NumberValue(0.0D);

	public NumberValue(double num) {
		super(num);
	}

	public NumberValue(Double num) {
		super(num);
	}

	public NumberValue(int num) {
		super((double) num);
	}

	public NumberValue(BigInteger bigInteger) {
		super(bigInteger.doubleValue());
	}

	public static boolean isNegativeZero(double d) {
		return Double.doubleToRawLongBits(d) == NEGATIVE_ZERO_BITS;
	}

	public static boolean isNegativeZero(Value<?> v) {
		return v instanceof final NumberValue n && isNegativeZero(n.value);
	}

	public static boolean isPositiveZero(double d) {
		return Double.doubleToRawLongBits(d) == POSITIVE_ZERO_BITS;
	}

	@NonStandard
	public boolean isEqualTo(NumberValue y, boolean negative_zero_equals_zero, boolean NaN_equals_NaN) {
		if (value.isNaN() && y.value.isNaN()) return NaN_equals_NaN;
		if (isNegativeZero(value) && isPositiveZero(y.value)) return negative_zero_equals_zero;
		if (isPositiveZero(value) && isNegativeZero(y.value)) return negative_zero_equals_zero;
		return value.doubleValue() == y.value.doubleValue();
	}

	public String toExactString() {
		if (Double.isFinite(value)) return new BigDecimal(value).toPlainString();
		return Double.toString(value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-tostring")
	public String stringValueOf(int radix) {
		if (radix == 10) return NumberToJSON.serializeNumber(value);

		// Doesn't always produce identical output to other engines, but should be good enough for the foreseeable future.
		double number = this.value;
		final boolean negative = number < 0;
		if (negative) number *= -1;

		double intPart = Math.floor(number);
		double decimalPart = number - intPart;
		final StringBuilder characters = new StringBuilder();

		if (intPart == 0) {
			characters.append('0');
		} else {
			while (intPart > 0) {
				characters.append(DIGITS.charAt((int) (intPart % radix)));
				intPart /= radix;
				intPart = Math.floor(intPart);
			}
		}

		if (negative) characters.append('-');
		characters.reverse();

		if (decimalPart != 0.0) {
			characters.append('.');

			// An approximation of the best formula, based on rough test data :^)
			final int precision = ((int) Math.ceil(Math.log(Math.pow(2, 150)) / Math.log(radix)));
			for (int i = 0; i < precision; ++i) {
				decimalPart *= radix;
				final long integral = (long) Math.floor(decimalPart);
				characters.append(DIGITS.charAt((int) integral));
				decimalPart -= integral;
			}

			while (characters.charAt(characters.length() - 1) == '0') {
				characters.deleteCharAt(characters.length() - 1);
			}

			if (characters.charAt(characters.length() - 1) == '.') {
				characters.deleteCharAt(characters.length() - 1);
			}
		}

		return characters.toString();
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return new StringValue(stringValueOf(10));
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.BRIGHT_YELLOW);
		builder.append(stringValueOf(10));
		builder.append(ANSI.RESET);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.of(!value.isNaN() && value != 0.0);
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) {
		return new NumberWrapper(interpreter.intrinsics, this);
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert %s to a BigInt".formatted(stringValueOf(10))));
	}

	@Override
	public String typeOf() {
		return "number";
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#eqn-truncate")
	public static double truncate(double x) {
		// The mathematical function truncate(x) removes the fractional part of x by rounding towards zero
		if (x < 0) {
			return Math.ceil(x);
		} else {
			return Math.floor(x);
		}
	}

	private static long modulo(long x, long y) {
		final long result = x % y;
		return result < 0 ? result + y : result;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toint32")
	public int toInt32() {
		// 1. Let number be ? ToNumber(argument).
		// 2. If number is not finite or number is either +0𝔽 or -0𝔽, return +0𝔽.
		if (!Double.isFinite(value) || value == 0) return 0;
		// 3. Let int be truncate(ℝ(number)).
		final long Int = (long) truncate(value);
		// 4. Let int32bit be int modulo 2^32.
		final long int32bit = modulo(Int, TWO_TO_THE_32);
		// 5. If int32bit ≥ 2^31, return 𝔽(int32bit - 2^32);
		if (int32bit >= TWO_TO_THE_31) return (int) (int32bit - TWO_TO_THE_32);
		// otherwise return 𝔽(int32bit).
		return (int) int32bit;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-touint32")
	public long toUint32() {
		// 1. Let number be ? ToNumber(argument).
		// 2. If number is NaN, +0𝔽, -0𝔽, +∞𝔽, or -∞𝔽, return +0𝔽.
		if (value.isNaN() || value == 0.0 || value.isInfinite()) return 0;
		// 3. Let int be the mathematical value whose sign is the sign of number
		// and whose magnitude is floor(abs(ℝ(number))).
		long int_ = ((long) Math.floor(Math.abs(value))) * (long) Math.signum(value);
		// 4. Let int32bit be int modulo 2^32.
		// 5. Return 𝔽(int32bit).
		return modulo(int_, TWO_TO_THE_32);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-lessThan")
	public BooleanValue lessThan(NumberValue other) {
		// 1. If x is NaN, return undefined.
		// 2. If y is NaN, return undefined.
		if (this.value.isNaN() || other.value.isNaN()) return null;
		return BooleanValue.of(this.value < other.value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isintegralnumber")
	public boolean isIntegralNumber() {
		// 1. If argument is not a Number, return false.
		// 2. If argument is not finite, return false.
		// 3. If truncate(ℝ(argument)) ≠ ℝ(argument), return false.
		// 4. Return true.
		return Double.isFinite(value) && truncate(value) == value;
	}

	public BigIntValue numberToBigInt(Interpreter interpreter) throws AbruptCompletion {
		// 1. If IsIntegralNumber(number) is false, throw a RangeError exception.
		if (!isIntegralNumber()) throw error(new RangeError(interpreter, "%s cannot be converted to BigInt because it is not an integer".formatted(stringValueOf(10))));
		// 2. Return ℤ(ℝ(number)).
		return new BigIntValue(new BigDecimal(value).toBigIntegerExact());
	}

}