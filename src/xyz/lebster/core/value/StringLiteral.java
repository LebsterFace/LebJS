package xyz.lebster.core.value;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.runtime.Interpreter;

public class StringLiteral extends Value<String> {
	public StringLiteral() {
		super(Type.String, "");
	}

	public StringLiteral(String value) {
		super(Type.String, value);
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
	public Function toFunction() throws NotImplementedException {
		throw new NotImplementedException("StringLiteral -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		final Dictionary result = new Dictionary();
		result.set("length", new NumericLiteral(value.length()));
		result.set("reverse", new NativeFunction((interpreter, arguments) -> new StringLiteral(new StringBuilder(value).reverse().toString())));

		return result;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("StringLiteral: '");
		System.out.print(value);
		System.out.println("'");
	}
}
