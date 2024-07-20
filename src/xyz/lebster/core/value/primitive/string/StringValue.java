package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.value.error.syntax.SyntaxErrorObject;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.PrimitiveIterator;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class StringValue extends Key<String> {
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
		if (value.equals("0")) return 0;
		if (value.startsWith("0")) return -1;
		if (value.isEmpty()) return -1;

		int index = 0;
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);
			if (c >= '0' && c <= '9') {
				index *= 10;
				index += c - '0';
			} else {
				return -1;
			}
		}

		return index;
	}

	@Override
	public boolean equalsKey(Key<?> other) {
		return other instanceof final StringValue stringValue && stringValue.value.equals(value);
	}

	@Override
	protected String displayColor() {
		return ANSI.GREEN;
	}

	@Override
	protected String rawDisplayString() {
		return StringEscapeUtils.quote(value, false);
	}

	@Override
	public void displayForObjectKey(StringBuilder builder) {
		if (isValidIdentifier(value)) {
			builder.append(value);
		} else {
			display(builder);
		}
	}

	public static boolean isValidIdentifier(String string) {
		final PrimitiveIterator.OfInt iterator = string.codePoints().iterator();
		if (iterator.hasNext() && Lexer.isIdentifierStart(iterator.next())) {
			while (iterator.hasNext()) {
				if (Lexer.isIdentifierPart(iterator.next())) {
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
	public void displayForConsoleLog(StringBuilder builder) {
		builder.append(this.value);
	}

	@Override
	@NonCompliant
	public NumberValue toNumberValue(Interpreter interpreter) {
		// FIXME: Follow spec
		if (value.isBlank()) return NumberValue.ZERO;

		final String string = value.trim();
		try {
			if (string.toLowerCase().startsWith("0b")) return new NumberValue(Integer.parseInt(string.substring(2), 2));
			if (string.toLowerCase().startsWith("0o")) return new NumberValue(Integer.parseInt(string.substring(2), 8));
			if (string.toLowerCase().startsWith("0x")) return new NumberValue(Integer.parseInt(string.substring(2), 16));
			return new NumberValue(Double.parseDouble(string));
		} catch (NumberFormatException e) {
			return NumberValue.NaN;
		}
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.of(!value.isEmpty());
	}

	@Override
	public StringWrapper toObjectValue(Interpreter interpreter) {
		return new StringWrapper(interpreter.intrinsics, this);
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let n be StringToBigInt(prim).
		final BigIntValue n = BigIntValue.stringToBigInt(value);
		// 2. If n is undefined, throw a SyntaxError exception.
		if (n == null) throw error(new SyntaxErrorObject(interpreter, "Cannot convert %s to a BigInt".formatted(StringEscapeUtils.quote(value, false))));
		// 3. Return n.
		return n;
	}

	@Override
	public String typeOf() {
		return "string";
	}
}