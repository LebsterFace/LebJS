package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.TypeError;

public final class Undefined extends Primitive<Void> {
	public Undefined() {
		super(null, Type.Undefined);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(false);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert undefined or null to object"));
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("undefined");
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