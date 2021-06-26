package xyz.lebster.core.value;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.exception.NotImplemented;

public class Undefined extends Value<Void> {
	public Undefined() {
		super(Type.Undefined, null);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("undefined");
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
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("Undefined -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		return new Dictionary();
	}
}
