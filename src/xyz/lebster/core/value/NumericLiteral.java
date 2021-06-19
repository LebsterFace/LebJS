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
}
