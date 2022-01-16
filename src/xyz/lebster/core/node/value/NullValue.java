package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.error.TypeError;

public final class NullValue extends PrimitiveValue<Void> {
	public static final NullValue instance = new NullValue();

	private NullValue() {
		super(null, Type.Null);
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return new StringValue("null");
	}

	@Override
	public void display(StringRepresentation builder) {
		builder.append("null");
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
	public String typeOf(Interpreter interpreter) {
		return "object";
	}
}