package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ArrayObject;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

public final class ArrayPrototype extends ObjectValue {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	static {
		instance.setMethod(Names.push, ArrayPrototype::push);
		instance.setMethod(Names.map, ArrayPrototype::map);
		instance.setMethod(Names.join, ArrayPrototype::join);
		instance.setMethod(Names.toString, ArrayPrototype::toStringMethod);
		instance.setMethod(Names.forEach, ArrayPrototype::forEach);
	}

	private ArrayPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.foreach")
	private static Value<?> forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		final Value<?> thisArg = arguments.length > 1 ? arguments[1] : UndefinedValue.instance;

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable<?> executable = Executable.getExecutable(callbackfn);
		// 4. Let k be 0.
		int k = 0;
		// 5. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final Value<?> kValue = O.get(interpreter, Pk);
				// ii. Perform ? Call(callbackfn, thisArg, « kValue, 𝔽(k), O »).
				executable.call(interpreter, thisArg, kValue, new NumberValue(k), O);
			}
			// d. Set k to k + 1.
			k = k + 1;
		}
		// 6. Return undefined.
		return UndefinedValue.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.tostring")
	private static Value<?> toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 1. Let array be ? ToObject(this value).
		final ObjectValue array = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let func be ? Get(array, "join").
		final Value<?> func = array.get(interpreter, Names.join);
		// 3. If IsCallable(func) is false, set func to the intrinsic function %Object.prototype.toString%.
		final Executable<?> f_Func = func instanceof Executable<?> e ? e : ObjectPrototype.toStringMethod;
		// 4. Return ? Call(func, array).
		return f_Func.call(interpreter, array);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.join")
	private static Value<?> join(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = Long.min(MAX_LENGTH, O.get(interpreter, Names.length).toNumberValue(interpreter).value.longValue());
		final boolean noSeparator = elements.length == 0 || elements[0].type == Type.Undefined;
		final String sep = noSeparator ? "," : elements[0].toStringValue(interpreter).value;

		final StringBuilder result = new StringBuilder();
		for (int k = 0; k < len; k++) {
			if (k > 0) result.append(sep);
			final Value<?> element = O.get(interpreter, new StringValue(k));
			result.append(element.isNullish() ? "" : element.toStringValue(interpreter).value);
		}

		return new StringValue(result.toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-lengthofarraylike")
	private static long lengthOfArrayLike(ObjectValue O, Interpreter interpreter) throws AbruptCompletion {
		final double number = O.get(interpreter, Names.length).toNumberValue(interpreter).value;
		if (Double.isNaN(number) || number <= 0) {
			return 0L;
		} else {
			return Long.min((long) number, MAX_LENGTH);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.push")
	private static Value<?> push(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);

		if ((len + elements.length) > MAX_LENGTH) {
			final String message = "Pushing " + elements.length + " elements on an array-like of length " + len +
								   " is disallowed, as the total surpasses 2^53-1";
			throw AbruptCompletion.error(new TypeError(message));
		}

		for (final Value<?> E : elements)
			O.set(interpreter, new StringValue(len), E);

		final NumberValue newLength = new NumberValue(len + elements.length);
		O.set(interpreter, Names.length, newLength);
		return newLength;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.map")
	private static Value<?> map(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		final Value<?> thisArg = arguments.length > 1 ? arguments[1] : UndefinedValue.instance;

		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);
		final var executable = Executable.getExecutable(callbackfn);

		final Value<?>[] values = new Value<?>[(int) len];
		for (int k = 0; k < len; k++) {
			final var Pk = new StringValue(k);
			if (O.hasOwnProperty(Pk)) {
				values[k] = executable.call(interpreter, thisArg, O.get(interpreter, Pk), new NumberValue(k), O);
			}
		}

		return new ArrayObject(values);
	}
}