package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;

public final class StringPrototype extends Dictionary {
	public static final StringPrototype instance = new StringPrototype();

	static {
		instance.setMethod("reverse", StringPrototype::reverse);
	}

	private StringPrototype() {
	}

	@NonStandard
	private static StringLiteral reverse(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final String S = interpreter.thisValue().toStringLiteral(interpreter).value;
		return new StringLiteral(new StringBuilder(S).reverse().toString());
	}
}