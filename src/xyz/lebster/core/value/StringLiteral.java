package xyz.lebster.core.value;

import xyz.lebster.exception.NotImplemented;

public class StringLiteral extends Value<String> {
	public StringLiteral() {
		super(Type.String, "");
	}

	public StringLiteral(String value) {
		super(Type.String, value);
	}

	@Override
	public String toString() {
//		FIXME: Should we escape this?
		return '"' + value + '"';
	}

	@Override
	public StringLiteral toStringLiteral() {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(value.length() > 0);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		if (value.isBlank()) return new NumericLiteral(0);

		try {
			return new NumericLiteral(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new NumericLiteral(Double.NaN);
		}
	}

	@Override
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("StringLiteral -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		return new StringWrapper(this);
	}
}
