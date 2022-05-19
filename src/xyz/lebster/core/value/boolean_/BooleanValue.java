package xyz.lebster.core.value.boolean_;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.PrimitiveValue;
import xyz.lebster.core.value.number.NumberValue;
import xyz.lebster.core.value.string.StringValue;

public final class BooleanValue extends PrimitiveValue<Boolean> {
	public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE);
	public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE);

	private BooleanValue(boolean value) {
		super(value);
	}

	public static BooleanValue of(boolean b) {
		return b ? TRUE : FALSE;
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_YELLOW);
		representation.append(value);
		representation.append(ANSI.RESET);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
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
	public BooleanWrapper toObjectValue(Interpreter interpreter) {
		return new BooleanWrapper(interpreter.intrinsics.booleanPrototype, this);
	}

	public BooleanValue not() {
		return this.value ? BooleanValue.FALSE : BooleanValue.TRUE;
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "boolean";
	}
}