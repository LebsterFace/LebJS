package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Value;

public final class StringPrototype extends ObjectValue {
	public static final StringPrototype instance = new StringPrototype();

	static {
		instance.setMethod("reverse", StringPrototype::reverse);
	}

	private StringPrototype() {
	}

	@NonStandard
	private static StringValue reverse(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final String S = interpreter.thisValue().toStringLiteral(interpreter).value;
		return new StringValue(new StringBuilder(S).reverse().toString());
	}
}