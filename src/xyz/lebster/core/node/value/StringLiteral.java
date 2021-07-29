package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.Interpreter;

public final class StringLiteral extends Primitive<String> {
	public StringLiteral(String value) {
		super(value, Type.String);
	}

	public StringLiteral(Object value) {
		super(String.valueOf(value), Type.String);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
//		FIXME: Follow spec
		try {
			return new NumericLiteral(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new NumericLiteral(Double.NaN);
		}
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(value.length() > 0);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		return new StringWrapper(this);
	}

	@Override
	public String typeOf() {
		return "string";
	}
}