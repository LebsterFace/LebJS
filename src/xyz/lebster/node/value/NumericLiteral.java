package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.Interpreter;

public class NumericLiteral extends Primitive<Double> {
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
		else if (d == 0.0 || d == -0.0) return "0";
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
	public NumericLiteral toNumericLiteral() {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		final boolean shouldBeFalse = value.isNaN() || value == 0.0 || value == -0.0;
		return new BooleanLiteral(!shouldBeFalse);
	}

	@Override
	public Dictionary toDictionary() {
		throw new NotImplemented("NumberWrapper");
	}

	@Override
	public String typeOf() {
		return "number";
	}

	public NumericLiteral unaryMinus() {
		return new NumericLiteral(-value);
	}

	@Override
	public String toString() {
		return stringValueOf(value);
	}
}