package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.TypeError;

public final class Null extends Primitive<Void> {
	public static final Null instance = new Null();

	private Null() {
		super(null, Type.Null);
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return new StringLiteral("null");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("null");
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return BooleanLiteral.FALSE;
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert null to object"));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Null");
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "object";
	}
}