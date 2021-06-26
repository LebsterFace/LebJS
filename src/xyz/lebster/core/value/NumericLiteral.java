package xyz.lebster.core.value;

import xyz.lebster.exception.NotImplemented;

public class NumericLiteral extends Value<Double> {
	public NumericLiteral(double value) {
		super(Type.Number, value);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(value != 0);
	}

	@Override
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("NumericLiteral -> Function ");
	}

	@Override
	public Dictionary toDictionary() throws NotImplemented {
		throw new NotImplemented("NumericLiteral -> Dictionary ");
	}

	@Override
	public String toString() {
		return stringify(value);
	}

	public static String stringify(double x) {
//		https://tc39.es/ecma262/#sec-numeric-types-number-tostring
		if (Double.isNaN(x)) return "NaN";
		else if (x == -0.0 || x == 0.0) return "0";
		else if (x < 0) return "-" + stringify(-x);
		else if (x == Double.POSITIVE_INFINITY) return "Infinity";

//		FIXME: Follow spec
		final String input = String.valueOf(x);
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
			if (decimalPosition + 1== firstZeros) {
				return input.substring(0, decimalPosition);
			} else {
				return input.substring(0, firstZeros);
			}
		}
	}
}
