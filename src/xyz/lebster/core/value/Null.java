package xyz.lebster.core.value;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.runtime.Interpreter;

public class Null extends Value<Void> {
	public Null() {
		super(Type.Null, null);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("null");
	}

	@Override
	public StringLiteral toStringLiteral() {
		return new StringLiteral("null");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(false);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(0);
	}

	@Override
	public Function toFunction() throws NotImplementedException {
		throw new NotImplementedException("Null -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		return new Dictionary();
	}
}