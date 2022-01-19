package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.StringWrapper;

public final class StringValue extends ObjectValue.Key<String> {
	public StringValue(String value) {
		super(value, Value.Type.String);
	}

	public StringValue(Object value) {
		super(String.valueOf(value), Value.Type.String);
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.GREEN);
		final char quoteType = this.value.contains("'") ? '"' : '\'';
		representation.append(quoteType);
		representation.append(value);
		representation.append(quoteType);
		representation.append(ANSI.RESET);
	}

	@Override
	protected void displayObjectKey(StringRepresentation representation) {
		if (this.value.contains(" ")) {
			this.display(representation);
		} else {
			representation.append(value);
		}
	}

	@Override
	public void displayForConsoleLog(StringRepresentation representation) {
		representation.append(this.value);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) {
		// FIXME: Follow spec
		if (value.isBlank())
			return new NumberValue(0);

		try {
			return new NumberValue(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new NumberValue(Double.NaN);
		}
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.of(value.length() > 0);
	}

	@Override
	public StringWrapper toObjectValue(Interpreter interpreter) {
		return new StringWrapper(this);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "string";
	}
}