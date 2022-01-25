package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public final class UndefinedValue extends PrimitiveValue<Void> {
	public static final UndefinedValue instance = new UndefinedValue();

	private UndefinedValue() {
		super(null, Value.Type.Undefined);
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
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_BLACK);
		representation.append("undefined");
		representation.append(ANSI.RESET);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "undefined";
	}
}