package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.node.value.*;

public final class StringPrototype extends Dictionary {
	public static final StringPrototype instance = new StringPrototype();

	private StringPrototype() {
		this.setMethod("reverse", (interpreter, arguments) -> {
			final Value<?> value = interpreter.thisValue();
			if (!(value instanceof final StringWrapper wrapper)) {
				throw new NotImplemented("StringPrototype.reverse for non-StringWrapper Value");
			}

			return new StringLiteral(new StringBuilder(wrapper.value.value).reverse().toString());
		});
	}
}