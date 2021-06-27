package xyz.lebster.core.value;

import xyz.lebster.exception.NotImplemented;

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
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("BooleanLiteral -> Function");
	}

	@Override
	public Dictionary toDictionary() throws NotImplemented {
		throw new NotImplemented("BooleanLiteral -> toDictionary");
	}

	public BooleanLiteral not() {
		return new BooleanLiteral(!value);
	}
}
