package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;

public class BooleanLiteral extends Primitive<Boolean> {
	public BooleanLiteral(boolean value) {
		super(value, Type.Boolean);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(value ? 1.0 : 0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return this;
	}

	@Override
	public Dictionary toDictionary() {
		throw new NotImplemented("BooleanWrapper");
	}

	public BooleanLiteral not() {
		return new BooleanLiteral(!value);
	}

	@Override
	public String typeOf() {
		return "boolean";
	}
}