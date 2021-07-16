package xyz.lebster.node.value;

import xyz.lebster.Dumper;
import xyz.lebster.runtime.TypeError;

public class Undefined extends Primitive<Void> {
	public Undefined() {
		super(null, Type.Undefined);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(false);
	}

	@Override
	public Dictionary toDictionary() {
		return new TypeError("Cannot convert undefined to base!");
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Undefined");
	}

	@Override
	public String toString() {
		return "undefined";
	}

	@Override
	public String typeOf() {
		return toString();
	}
}