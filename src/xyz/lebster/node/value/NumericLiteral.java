package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.Interpreter;

public class NumericLiteral extends Primitive<Double> {
	public NumericLiteral(double value) {
		super(value, Type.Number);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		final boolean shouldBeFalse = value.isNaN() || value == 0.0 || value == -0.0;
		return new BooleanLiteral(!shouldBeFalse);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		throw new NotImplemented("NumberWrapper");
	}

	public NumericLiteral unaryMinus() {
		return new NumericLiteral(-value);
	}
}
