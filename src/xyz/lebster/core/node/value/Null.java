package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.TypeError;

public final class Null extends Primitive<Void> {
	private Null() {
		super(null, Type.Null);
	}

	public static final Null instance = new Null();

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(false);
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
	public String typeOf() {
		return "object";
	}
}