package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;

public final class StringLiteral extends Dictionary.Key<String> {
	public StringLiteral(String value) {
		super(value, Type.String);
	}

	public StringLiteral(Object value) {
		super(String.valueOf(value), Type.String);
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public void toConsoleLog(StringBuilder builder) {
		final char quoteType = this.value.contains("'") ? '"' : '\'';
		builder.append(ANSI.GREEN);
		builder.append(quoteType);
		builder.append(value);
		builder.append(quoteType);
		builder.append(ANSI.RESET);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		// FIXME: Follow spec
		if (value.isBlank())
			return new NumericLiteral(0);

		try {
			return new NumericLiteral(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new NumericLiteral(Double.NaN);
		}
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return BooleanLiteral.of(value.length() > 0);
	}

	@Override
	public StringWrapper toDictionary(Interpreter interpreter) {
		return new StringWrapper(this);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "string";
	}
}