package xyz.lebster.core.value.globals;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class Null extends PrimitiveValue<Void> {
	public static final Null instance = new Null();

	private Null() {
		super(null);
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return Names.null_;
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.BRIGHT_WHITE);
		builder.append("null");
		builder.append(ANSI.RESET);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return NumberValue.ZERO;
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.FALSE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert null to object"));
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert null to a BigInt"));
	}

	@Override
	public String typeOf() {
		return "object";
	}
}