package xyz.lebster.node.value;

public class StringLiteral extends Primitive<String> {
	public StringLiteral(String value) {
		super(value, Type.String);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public StringLiteral toStringLiteral() {
		return this;
	}

	@Override
	public NumericLiteral toNumericLiteral() {
//		FIXME: Follow spec
		return new NumericLiteral(Double.parseDouble(value));
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(value.length() > 0);
	}

	@Override
	public Dictionary toDictionary() {
		return new StringWrapper(this);
	}

	@Override
	public String typeOf() {
		return "string";
	}
}