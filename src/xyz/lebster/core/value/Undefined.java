package xyz.lebster.core.value;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.exception.NotImplementedException;

public class Undefined extends Value<Void> {
	public Undefined() {
		super(Type.Undefined, null);
	}

	@Override
	public StringLiteral toStringLiteral() {
		return new StringLiteral("undefined");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(false);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public Function toFunction() throws NotImplementedException {
		throw new NotImplementedException("Undefined -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		return new Dictionary();
	}
}
