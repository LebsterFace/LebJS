package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberValue extends PrimitiveValue<Double> {
	public static final long TWO_TO_THE_31 = 2147483648L;
	public static final long TWO_TO_THE_32 = 4294967296L;
	public static final long UINT32_LIMIT = TWO_TO_THE_32 - 1;

	public static final long NEGATIVE_ZERO_BITS = 0x8000000000000000L;
	public static final long POSITIVE_ZERO_BITS = 0;
	public static final NumberValue NaN = new NumberValue(Double.NaN);
	public static final NumberValue ZERO = new NumberValue(0.0D);
	public static final NumberValue ONE = new NumberValue(1.0D);
	public static final NumberValue MINUS_ONE = new NumberValue(-1.0D);

	public NumberValue(double num) {
		super(num);
	}

	public NumberValue(Double num) {
		super(num);
	}

	public NumberValue(int num) {
		super((double) num);
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

	public static boolean isPositiveZero(Value<?> v) {
		return v instanceof final NumberValue n && isPositiveZero(n.value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-tostring")
	@NonCompliant
	public static String stringValueOf(Double d) {
		if (d == 0.0) return "0";
		if (d.isNaN()) return "NaN";
		else if (d < 0.0) return "-" + stringValueOf(-d);
		else if (d.isInfinite()) return "Infinity";
		// Scientific notation is used if the number's magnitude (ignoring sign)
		// is greater than or equal to 10^21 or less than 10^-6
		if (d >= Math.pow(10, 21) || d < Math.pow(10, -6)) {
			return String.valueOf(d).toLowerCase();
		} else {
			return new BigDecimal(d).setScale(17, RoundingMode.FLOOR).stripTrailingZeros().toPlainString();
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-sameValueZero")
	public static boolean sameValueZero(NumberValue x, NumberValue y) {
		// 6.1.6.1.15 Number::sameValueZero ( x, y )

		// 1. If x is NaN and y is NaN, return true.
		if (x.value.isNaN() && y.value.isNaN()) return true;
		// 2. If x is +0𝔽 and y is -0𝔽, return true.
		if (x.value == 0 && y.value == -0) return true;
		// 3. If x is -0𝔽 and y is +0𝔽, return true.
		// 4. If x is the same Number value as y, return true.
		// 5. Return false.
		return x.value.equals(y.value);
	}

	public String stringValueOf() {
		return NumberValue.stringValueOf(this.value);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return new StringValue(stringValueOf(value));
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_YELLOW);
		representation.append(stringValueOf(value));
		representation.append(ANSI.RESET);
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
	public String typeOf(Interpreter interpreter) {
		return "number";
	}

	public NumberValue unaryMinus() {
		return new NumberValue(-value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-bitwiseNOT")
	public NumberValue bitwiseNOT() {
		// 1. Let oldValue be ! ToInt32(x).
		final int oldValue = toInt32();
		// 2. Return the result of applying bitwise complement to oldValue.
		// The mathematical value of the result is exactly representable as a 32-bit two's complement bit string.
		return new NumberValue(~oldValue);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toint32")
	public int toInt32() {
		// 1. Let number be ? ToNumber(argument).
		// 2. If number is NaN, +0𝔽, -0𝔽, +∞𝔽, or -∞𝔽, return +0𝔽.
		if (value.isNaN() || value == 0.0 || value.isInfinite()) return 0;
		// 3. Let int be the mathematical value whose sign is the sign of number
		// and whose magnitude is floor(abs(ℝ(number))).
		long int_ = ((long) Math.floor(Math.abs(value))) * (long) Math.signum(value);
		// 4. Let int32bit be int modulo 2^32.
		long int32bit = int_ % TWO_TO_THE_32;
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
		return int_ % TWO_TO_THE_32;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-lessThan")
	public BooleanValue lessThan(NumberValue other) {
		// 1. If x is NaN, return undefined.
		// 2. If y is NaN, return undefined.
		if (this.value.isNaN() || other.value.isNaN()) return null;
		return BooleanValue.of(this.value < other.value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-number-sameValue")
	public boolean sameValue(NumberValue y) {
		// 1. If x is NaN and y is NaN, return true.
		if (this.value.isNaN() && y.value.isNaN()) return true;
		// 2. If x is +0𝔽 and y is -0𝔽, return false.
		if (isPositiveZero(this) && isNegativeZero(y)) return false;
		// 3. If x is -0𝔽 and y is +0𝔽, return false.
		if (isNegativeZero(this) && isPositiveZero(y)) return false;
		// 4. If x is the same Number value as y, return true.
		// 5. Return false.
		return this.value.doubleValue() == y.value.doubleValue();
	}
}