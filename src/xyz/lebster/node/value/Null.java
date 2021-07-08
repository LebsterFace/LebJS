package xyz.lebster.node.value;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.runtime.TypeError;

public class Null extends Primitive<Void> {
	public Null() {
		super(null, Type.Null);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(false);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		return new TypeError("Cannot convert null to base!");
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Null");
	}

	@Override
	public String typeOf() {
		return "object";
	}
}
