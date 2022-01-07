package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.TypeError;

public final class Null extends Primitive<Void> {
	public static final Null instance = new Null();

	private Null() {
		super(null, Type.Null);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return new StringValue("null");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("null");
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return new NumberValue(0.0);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.FALSE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
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