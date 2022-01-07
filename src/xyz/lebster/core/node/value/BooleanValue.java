package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.object.ObjectValue;

public final class BooleanValue extends Primitive<Boolean> {
	public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE);
	public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE);

	private BooleanValue(boolean value) {
		super(value, Type.Boolean);
	}

	public static BooleanValue of(boolean b) {
		return b ? TRUE : FALSE;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_YELLOW);
		representation.append(value);
		representation.append(ANSI.RESET);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return new StringValue(value.toString());
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return new NumberValue(value ? 1.0 : 0.0);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) {
		throw new NotImplemented("BooleanWrapper");
	}

	public BooleanValue not() {
		return new BooleanValue(!value);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "boolean";
	}
}