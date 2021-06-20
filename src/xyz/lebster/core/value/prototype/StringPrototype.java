package xyz.lebster.core.value.prototype;

import xyz.lebster.core.value.*;
import xyz.lebster.exception.NotImplemented;

public class StringPrototype extends Dictionary {
	public static final StringPrototype instance = new StringPrototype();

	public StringPrototype() {
		set("reverse", new NativeFunction((interpreter, arguments) -> {
			final Value<?> value = interpreter.thisValue();
			if (!(value instanceof final StringWrapper wrapper)) {
				throw new NotImplemented("StringPrototype.reverse for non-StringWrapper Value");
			}

			return new StringLiteral(new StringBuilder(wrapper.value.value).reverse().toString());
		}));
	}
}
