package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.TypeError;

public final class Undefined extends Primitive<Void> {
	private Undefined() {
		super(null, Type.Undefined);
	}

	public static final Undefined instance = new Undefined();

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return new StringLiteral("undefined");
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
		throw AbruptCompletion.error(new TypeError("Cannot convert undefined to object"));
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_BLACK);
		representation.append("undefined");
		representation.append(ANSI.RESET);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Undefined");
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "undefined";
	}
}