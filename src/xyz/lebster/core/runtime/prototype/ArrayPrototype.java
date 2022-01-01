package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.ArrayObject;
import xyz.lebster.core.runtime.TypeError;

public final class ArrayPrototype extends Dictionary {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L;

	private ArrayPrototype() {
		// https://tc39.es/ecma262/multipage#sec-array.prototype.push
		this.setMethod("push", (interpreter, elements) -> {
			final Dictionary O = interpreter.thisValue().toDictionary(interpreter);
			final long len = Long.min(MAX_LENGTH, O.get(ArrayObject.LENGTH_KEY).toNumericLiteral(interpreter).value.longValue());

			if ((len + elements.length) > MAX_LENGTH) {
				throw AbruptCompletion.error(new TypeError("Pushing " + elements.length + " elements on an array-like of length "
														   + len + " is disallowed, as the total surpasses 2**53-1"));
			}

			for (final Value<?> E : elements)
				O.set(interpreter, new StringLiteral(len), E);

			final NumericLiteral newLength = new NumericLiteral(len + elements.length);
			O.set(interpreter, ArrayObject.LENGTH_KEY, newLength);
			return newLength;
		});

		// https://tc39.es/ecma262/multipage#sec-array.prototype.join
		final NativeFunction join = new NativeFunction((interpreter, elements) -> {
			final Dictionary O = interpreter.thisValue().toDictionary(interpreter);
			final long len = Long.min(MAX_LENGTH, O.get(ArrayObject.LENGTH_KEY).toNumericLiteral(interpreter).value.longValue());
			final boolean noSeparator = elements.length == 0 || elements[0].type == Type.Undefined;
			final String sep = noSeparator ? "," : elements[0].toStringLiteral(interpreter).value;

			final StringBuilder result = new StringBuilder();
			for (int k = 0; k < len; k++) {
				if (k > 0) result.append(sep);
				final Value<?> element = O.get(new StringLiteral(k));
				result.append(element.isNullish() ? "" : element.toStringLiteral(interpreter).value);
			}

			return new StringLiteral(result.toString());
		});

		put("join", join);

		// FIXME: Follow spec
		// https://tc39.es/ecma262/multipage#sec-array.prototype.tostring
		put("toString", join);
	}
}