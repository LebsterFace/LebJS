package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.ArrayConstructor;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.object.ArrayObject;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.*;

import java.util.Arrays;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;
import static xyz.lebster.core.runtime.value.native_.NativeFunction.argumentString;

public final class ArrayPrototype extends ObjectValue {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	static {
		instance.put(Names.constructor, ArrayConstructor.instance);
		instance.putMethod(Names.push, ArrayPrototype::push);
		instance.putMethod(Names.map, ArrayPrototype::map);
		instance.putMethod(Names.reduce, ArrayPrototype::reduce);
		instance.putMethod(Names.filter, ArrayPrototype::filter);
		instance.putMethod(Names.join, ArrayPrototype::join);
		instance.putMethod(Names.toString, ArrayPrototype::toStringMethod);
		instance.putMethod(Names.forEach, ArrayPrototype::forEach);

		final var values = new NativeFunction(Names.values, ArrayPrototype::values);
		instance.put(Names.values, values);
		instance.put(SymbolValue.iterator, values);
	}

	private ArrayPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.filter")
	private static ArrayObject filter(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.8 Array.prototype.filter ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable executable = Executable.getExecutable(callbackfn);
		// 4. Let A be ? ArraySpeciesCreate(O, 0).
		final Value<?>[] A = new Value<?>[(int) len];
		// 5. Let k be 0.
		int k = 0;
		// 6. Let `to` be 0.
		int to = 0;
		// 7. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final Value<?> kValue = O.get(interpreter, Pk);
				// ii. Let selected be ! ToBoolean(? Call(callbackfn, thisArg, « kValue, 𝔽(k), O »)).
				final boolean selected = executable.call(interpreter, thisArg, kValue, new NumberValue(k), O).isTruthy(interpreter);
				// iii. If selected is true, then
				if (selected) {
					// 1. Perform ? CreateDataPropertyOrThrow(A, ! ToString(𝔽(to)), kValue).
					A[to] = kValue;
					// 2. Set `to` to `to` + 1.
					to = to + 1;
				}
			}

			// d. Set k to k + 1.
			k = k + 1;
		}

		// 8. Return A.
		return new ArrayObject(Arrays.copyOfRange(A, 0, to));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.values")
	@NonCompliant
	private static ArrayIterator values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return new ArrayIterator(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.foreach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable executable = Executable.getExecutable(callbackfn);
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
		return Undefined.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.tostring")
	private static Value<?> toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let array be ? ToObject(this value).
		final ObjectValue array = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let func be ? Get(array, "join").
		final Value<?> func = array.get(interpreter, Names.join);
		// 3. If IsCallable(func) is false, set func to the intrinsic function %Object.prototype.toString%.
		final Executable f_Func = func instanceof Executable e ? e : ObjectPrototype.toStringMethod;
		// 4. Return ? Call(func, array).
		return f_Func.call(interpreter, array);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.join")
	private static StringValue join(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.16 Array.prototype.join ( separator )
		final String sep = argumentString(0, ",", interpreter, arguments);

		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final NumberValue lengthProperty = O.get(interpreter, Names.length).toNumberValue(interpreter);
		final long len = Long.min(MAX_LENGTH, lengthProperty.value.longValue());

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
	private static NumberValue push(Interpreter interpreter, Value<?>[] items) throws AbruptCompletion {
		// 23.1.3.21 Array.prototype.push ( ...items )
		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		long len = lengthOfArrayLike(O, interpreter);
		// 3. Let argCount be the number of elements in items.
		final int argCount = items.length;
		// 4. If len + argCount > 2^53 - 1, throw a TypeError exception.
		if (len + argCount > MAX_LENGTH) {
			throw AbruptCompletion.error(new TypeError("Pushing " + argCount + " elements on an array-like of length " +
													   len + " is disallowed, as the total surpasses 2^53-1"));
		}

		// 5. For each element E of items, do
		for (final Value<?> E : items) {
			// a. Perform ? Set(O, ! ToString(𝔽(len)), E, true).
			O.set(interpreter, new StringValue(len), E);
			// b. Set len to len + 1.
			len += 1;
		}

		// 6. Perform ? Set(O, "length", 𝔽(len), true).
		final var newLen = new NumberValue(len);
		O.set(interpreter, Names.length, newLen);
		// 7. Return 𝔽(len).
		return newLen;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.map")
	private static ArrayObject map(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);
		final Executable executable = Executable.getExecutable(callbackfn);

		final Value<?>[] values = new Value<?>[(int) len];
		for (int k = 0; k < len; k++) {
			final var Pk = new StringValue(k);
			if (O.hasOwnProperty(Pk)) {
				values[k] = executable.call(interpreter, thisArg, O.get(interpreter, Pk), new NumberValue(k), O);
			}
		}

		return new ArrayObject(values);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.reduce")
	private static Value<?> reduce(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.22 Array.prototype.reduce ( callbackfn [ , initialValue ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> initialValue = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callback = Executable.getExecutable(callbackfn);
		// 4. If len = 0 and initialValue is not present, throw a TypeError exception.
		if (len == 0 && arguments.length == 1)
			throw AbruptCompletion.error(new TypeError("Reduce of empty array with no initial value"));

		// 5. Let k be 0.
		int k = 0;
		// 6. Let accumulator be undefined.
		Value<?> accumulator = Undefined.instance;
		// 7. If initialValue is present, then
		if (arguments.length > 1) {
			// a. Set accumulator to initialValue.
			accumulator = initialValue;
		}
		// 8. Else,
		else {
			// a. Let kPresent be false.
			boolean kPresent = false;
			// b. Repeat, while kPresent is false and k < len,
			while (!kPresent && k < len) {
				// i. Let Pk be ! ToString(𝔽(k)).
				final StringValue Pk = new StringValue(k);
				// ii. Set kPresent to ? HasProperty(O, Pk).
				kPresent = O.hasProperty(Pk);
				// iii. If kPresent is true, then
				if (kPresent) {
					// 1. Set accumulator to ? Get(O, Pk).
					accumulator = O.get(interpreter, Pk);
				}
				// iv. Set k to k + 1.
				k = k + 1;
			}
			// c. If kPresent is false, throw a TypeError exception.
			if (!kPresent)
				throw AbruptCompletion.error(new TypeError("Reduce of empty array with no initial value"));
		}

		// 9. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final var kValue = O.get(interpreter, Pk);
				// ii. Set accumulator to ? Call(callbackfn, undefined, « accumulator, kValue, 𝔽(k), O »).
				accumulator = callback.call(interpreter, Undefined.instance, accumulator, kValue, new NumberValue(k), O);
			}

			// d. Set k to k + 1.
			k = k + 1;
		}

		// 10. Return accumulator.
		return accumulator;
	}

	private static class ArrayIterator extends ObjectValue {
		private final ObjectValue O;
		private final long len;
		private int index;

		public ArrayIterator(Interpreter $) throws AbruptCompletion {
			this.O = $.thisValue().toObjectValue($);
			this.len = lengthOfArrayLike(O, $);
			this.index = 0;
			this.putMethod(Names.next, (__, ___) -> {
				final ObjectValue result = new ObjectValue();
				result.put(Names.value, index > len ? Undefined.instance : O.get($, new StringValue(index++)));
				result.put(Names.done, BooleanValue.of(index > len));
				return result;
			});
		}
	}
}