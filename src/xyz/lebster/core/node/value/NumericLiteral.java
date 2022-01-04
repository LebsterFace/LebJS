package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;

public final class NumericLiteral extends Primitive<Double> {
	public static final long NEGATIVE_ZERO_BITS = 0x8000000000000000L;
	public static final long POSITIVE_ZERO_BITS = 0;

	public static boolean isNegativeZero(double d) { return Double.doubleToRawLongBits(d) == NEGATIVE_ZERO_BITS; }
	public static boolean isNegativeZero(Double d) { return Double.doubleToRawLongBits(d) == NEGATIVE_ZERO_BITS; }
	public static boolean isNegativeZero(NumericLiteral n) { return isNegativeZero(n.value); }

	public static boolean isPositiveZero(double d) { return Double.doubleToRawLongBits(d) == POSITIVE_ZERO_BITS; }
	public static boolean isPositiveZero(Double d) { return Double.doubleToRawLongBits(d) == POSITIVE_ZERO_BITS; }
	public static boolean isPositiveZero(NumericLiteral n) { return isPositiveZero(n.value); }

	public NumericLiteral(double num) {
		super(num, Type.Number);
	}

	public NumericLiteral(Double num) {
		super(num, Type.Number);
	}

	public NumericLiteral(int num) {
		super((double) num, Type.Number);
	}

	private static String stringValueOf(Double d) {
		if (d.isNaN()) return "NaN";
		else if (d == 0.0) return "0";
		else if (d < 0.0) return "-" + stringValueOf(-d);
		else if (d.isInfinite()) return "Infinity";
		final String input = String.valueOf(d);
		int decimalPosition = -1;
		int firstZeros = -1;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '.') {
				decimalPosition = i;
			} else if (decimalPosition != -1) {
				if (input.charAt(i) == '0') {
					if (firstZeros == -1) {
						firstZeros = i;
					}
				} else {
					firstZeros = -1;
				}
			}
		}

		if (decimalPosition == -1 || firstZeros == -1) {
			return input;
		} else {
			if (decimalPosition + 1 == firstZeros) {
				return input.substring(0, decimalPosition);
			} else {
				return input.substring(0, firstZeros);
			}
		}
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return new StringLiteral(stringValueOf(value));
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_YELLOW);
		representation.append(stringValueOf(value));
		representation.append(ANSI.RESET);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		final boolean shouldBeFalse = value.isNaN() || value == 0.0;
		return new BooleanLiteral(!shouldBeFalse);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		throw new NotImplemented("NumberWrapper");
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "number";
	}

	public NumericLiteral unaryMinus() {
		return new NumericLiteral(-value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toint32")
	public int toInt32() {
		// 1. Let number be ? ToNumber(argument).
		// 2. If number is NaN, +0𝔽, -0𝔽, +∞𝔽, or -∞𝔽, return +0𝔽.
		if (value.isNaN() || value == 0.0 || value.isInfinite()) return 0;
		// 3. Let int be the mathematical value whose sign is the sign of number and whose magnitude is floor(abs(ℝ(number))).
		// 4. Let int32bit be int modulo 2^32.
		long int32bit = ((long) Math.floor(Math.abs(value))) % 4294967296L;
		// 5. If int32bit ≥ 2^31, return 𝔽(int32bit - 2^32);
		if (int32bit >= 2147483648L) return (int) (int32bit - 4294967296L);
		// otherwise return 𝔽(int32bit).
		return (int) int32bit;
	}
}