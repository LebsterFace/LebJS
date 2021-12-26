package xyz.lebster.core.node.value;

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
	public String toString(Interpreter interpreter) {
		return stringValueOf(value);
	}

	@Override
	public String toStringWithoutSideEffects() {
		return stringValueOf(value);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(stringValueOf(value));
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
	public String typeOf() {
		return "number";
	}

	public NumericLiteral unaryMinus() {
		return new NumericLiteral(-value);
	}
}