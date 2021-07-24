package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.runtime.TypeError;

public final class Null extends Primitive<Void> {
	public Null() {
		super(null, Type.Null);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(false);
	}

	@Override
	public Dictionary toDictionary() {
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