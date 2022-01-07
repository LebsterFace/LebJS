package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.TypeError;

public final class Undefined extends Primitive<Void> {
	public static final Undefined instance = new Undefined();

	private Undefined() {
		super(null, Type.Undefined);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return new StringValue("undefined");
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return new NumberValue(Double.NaN);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.FALSE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
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