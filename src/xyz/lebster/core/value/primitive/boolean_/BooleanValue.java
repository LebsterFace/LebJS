package xyz.lebster.core.value.primitive.boolean_;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public final class BooleanValue extends PrimitiveValue<Boolean> {
	public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE, Names.true_);
	public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE, Names.false_);

	public final StringValue stringValue;

	private BooleanValue(boolean value, StringValue stringValue) {
		super(value);
		this.stringValue = stringValue;
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
		return value ? NumberValue.ONE : NumberValue.ZERO;
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public BooleanWrapper toObjectValue(Interpreter interpreter) {
		return new BooleanWrapper(interpreter.intrinsics, this);
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) {
		return this.value ? BigIntValue.ONE : BigIntValue.ZERO;
	}

	public BooleanValue not() {
		return this.value ? BooleanValue.FALSE : BooleanValue.TRUE;
	}

	@Override
	public String typeOf() {
		return "boolean";
	}
}