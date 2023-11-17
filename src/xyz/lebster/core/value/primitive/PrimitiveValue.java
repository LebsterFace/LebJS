package xyz.lebster.core.value.primitive;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value) {
		super(value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tobigint")
	public abstract BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion;

	protected abstract String displayColor();
	protected abstract String rawDisplayString();

	@Override
	public final void display(StringBuilder builder) {
		builder.append(displayColor());
		builder.append(rawDisplayString());
		builder.append(ANSI.RESET);
	}
}