package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.StringWrapper;

public final class StringValue extends ObjectValue.Key<String> {
	public static final StringValue EMPTY = new StringValue("");

	public StringValue(String value) {
		super(value);
	}

	public StringValue(char value) {
		super(Character.toString(value));
	}

	public StringValue(int value) {
		super(Integer.toString(value));
	}

	public StringValue(long value) {
		super(Long.toString(value));
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public StringValue toFunctionName() {
		return this;
	}

	@Override
	public int toIndex() {
		if (value.length() == 0) return -1;

		int index = 0;
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			if (c < '0' || c > '9') return -1;

			index *= 10;
			index += c - '0';
		}

		return index;
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
	public void displayForObjectKey(StringRepresentation representation) {
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
	@NonCompliant
	public NumberValue toNumberValue(Interpreter interpreter) {
		// FIXME: Follow spec
		if (value.isBlank())
			return new NumberValue(0);

		try {
			return new NumberValue(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return NumberValue.NaN;
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