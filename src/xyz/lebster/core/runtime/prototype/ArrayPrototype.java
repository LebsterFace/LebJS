package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.ArrayObject;
import xyz.lebster.core.runtime.TypeError;

public final class ArrayPrototype extends Dictionary {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	static {
		instance.setMethod("push", ArrayPrototype::push);
		instance.setMethod("map", ArrayPrototype::map);
		instance.setMethod("join", ArrayPrototype::join);
		// FIXME: Follow spec (https://tc39.es/ecma262/multipage#sec-array.prototype.tostring)
		instance.setMethod("toString", ArrayPrototype::join);
	}

	private ArrayPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.join")
	private static Value<?> join(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
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
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-lengthofarraylike")
	private static long lengthOfArrayLike(Dictionary O, Interpreter interpreter) throws AbruptCompletion {
		final double number = O.get(ArrayObject.LENGTH_KEY).toNumericLiteral(interpreter).value;
		if (Double.isNaN(number) || number <= 0) {
			return 0L;
		} else {
			return Long.min((long) number, MAX_LENGTH);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.push")
	private static Value<?> push(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final Dictionary O = interpreter.thisValue().toDictionary(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);

		if ((len + elements.length) > MAX_LENGTH) {
			final String message = "Pushing " + elements.length + " elements on an array-like of length " + len +
								   " is disallowed, as the total surpasses 2^53-1";
			throw AbruptCompletion.error(new TypeError(message));
		}

		for (final Value<?> E : elements)
			O.set(interpreter, new StringLiteral(len), E);

		final NumericLiteral newLength = new NumericLiteral(len + elements.length);
		O.set(interpreter, ArrayObject.LENGTH_KEY, newLength);
		return newLength;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.map")
	private static Value<?> map(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = arguments.length > 0 ? arguments[0] : Undefined.instance;
		final Value<?> thisArg = arguments.length > 1 ? arguments[1] : Undefined.instance;

		final Dictionary O = interpreter.thisValue().toDictionary(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);

		if (!(callbackfn instanceof final Executable<?> executable)) {
			final StringRepresentation representation = new StringRepresentation();
			callbackfn.represent(representation);
			representation.append(" is not a function");
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}

		final Value<?>[] values = new Value<?>[(int) len];
		for (int k = 0; k < len; k++) {
			final var Pk = new StringLiteral(k);
			if (O.hasOwnProperty(Pk)) {
				values[k] = executable.call(interpreter, thisArg, O.get(Pk), new NumericLiteral(k), O);
			}
		}

		return new ArrayObject(values);
	}
}