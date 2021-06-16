package xyz.lebster.core.value;

import xyz.lebster.core.exception.NotImplementedException;

public class BooleanLiteral extends Value<Boolean> {
	public BooleanLiteral(Boolean value) {
		super(Type.Boolean, value);
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return this;
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(value ? 1 : 0);
	}

	@Override
	public Function toFunction() throws NotImplementedException {
		throw new NotImplementedException("BooleanLiteral -> Function");
	}

	@Override
	public Dictionary toDictionary() throws NotImplementedException {
		throw new NotImplementedException("BooleanLiteral -> toDictionary");
	}
}
