package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.PrimitiveIterator;

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
	public boolean equalsKey(ObjectValue.Key<?> other) {
		return other instanceof final StringValue stringValue && stringValue.value.equals(value);
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.GREEN);
		representation.append(StringEscapeUtils.quote(value, false));
		representation.append(ANSI.RESET);
	}

	@Override
	public void displayForObjectKey(StringRepresentation representation) {
		if (this.isValidIdentifier()) {
			representation.append(value);
		} else {
			this.display(representation);
		}
	}

	private boolean isValidIdentifier() {
		final PrimitiveIterator.OfInt iterator = value.codePoints().iterator();
		if (iterator.hasNext() && Lexer.isIdentifierStart(iterator.next())) {
			while (iterator.hasNext()) {
				if (Lexer.isIdentifierMiddle(iterator.next())) {
					continue;
				}

				return false;
			}

			return true;
		} else {
			return false;
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
			return NumberValue.ZERO;

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
		return new StringWrapper(interpreter.intrinsics, this);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "string";
	}
}