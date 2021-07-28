package xyz.lebster.core.node.value;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;

public final class NumericLiteral extends Primitive<Double> {
	public NumericLiteral(double num) {
		super(num, Type.Number);
	}

	public NumericLiteral(Double num) {
		super(num, Type.Number);
	}

	public NumericLiteral(int num) {
		super((double) num, Type.Number);
	}

	@Override
	public String toString(Interpreter interpreter) {
		return stringValueOf(value);
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
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		final boolean shouldBeFalse = value.isNaN() || value == 0.0 || value == -0.0;
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