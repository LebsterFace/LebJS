package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.ArrayObject;
import xyz.lebster.core.runtime.TypeError;

public final class ArrayPrototype extends ObjectLiteral {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	public static final StringLiteral push = new StringLiteral("push");
	public static final StringLiteral map = new StringLiteral("map");
	public static final StringLiteral join = new StringLiteral("join");

	static {
		instance.setMethod(push, ArrayPrototype::push);
		instance.setMethod(map, ArrayPrototype::map);
		instance.setMethod(join, ArrayPrototype::join);
		instance.setMethod(ObjectPrototype.toString, ArrayPrototype::toStringMethod);
	}

	private ArrayPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.tostring")
	private static Value<?> toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 1. Let array be ? ToObject(this value).
		final ObjectLiteral array = interpreter.thisValue().toObjectLiteral(interpreter);
		// 2. Let func be ? Get(array, "join").
		final Value<?> func = array.get(join);
		// 3. If IsCallable(func) is false, set func to the intrinsic function %Object.prototype.toString%.
		final Executable<?> f_Func = func instanceof Executable<?> e ? e : ObjectPrototype.toStringMethod;
		// 4. Return ? Call(func, array).
		return f_Func.call(interpreter, array);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.join")
	private static Value<?> join(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectLiteral O = interpreter.thisValue().toObjectLiteral(interpreter);
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
	private static long lengthOfArrayLike(ObjectLiteral O, Interpreter interpreter) throws AbruptCompletion {
		final double number = O.get(ArrayObject.LENGTH_KEY).toNumericLiteral(interpreter).value;
		if (Double.isNaN(number) || number <= 0) {
			return 0L;
		} else {
			return Long.min((long) number, MAX_LENGTH);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.push")
	private static Value<?> push(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectLiteral O = interpreter.thisValue().toObjectLiteral(interpreter);
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

		final ObjectLiteral O = interpreter.thisValue().toObjectLiteral(interpreter);
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