package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.Interpreter;

public class StringLiteral extends Primitive<String> {
	public StringLiteral(String value) {
		super(value, Type.String);
	}

	@Override
	public String toString() {
		return '"' + value + '"';
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		throw new NotImplemented("StringLiteral -> NumericLiteral");
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(value.length() > 0);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		return new StringWrapper(this);
	}
}
