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

public final class Undefined extends PrimitiveValue<Void> {
	public static final Undefined instance = new Undefined();

	private Undefined() {
		super(null);
	}

	@Override
	public String toString() {
		return "undefined";
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return Names.undefined;
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		return NumberValue.NaN;
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.FALSE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert undefined to object"));
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert undefined to a BigInt"));
	}

	@Override
	protected String displayColor() {
		return ANSI.BRIGHT_BLACK;
	}

	@Override
	protected String rawDisplayString() {
		return "undefined";
	}

	@Override
	public String typeOf() {
		return "undefined";
	}
}