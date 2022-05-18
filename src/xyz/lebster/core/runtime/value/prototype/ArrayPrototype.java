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

public final class ArrayPrototype extends BuiltinPrototype<ArrayObject, ArrayConstructor> {
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	public ArrayPrototype(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype);
		putMethod(fp, Names.push, ArrayPrototype::push);
		putMethod(fp, Names.map, ArrayPrototype::map);
		putMethod(fp, Names.reduce, ArrayPrototype::reduce);
		putMethod(fp, Names.filter, ArrayPrototype::filter);
		putMethod(fp, Names.join, ArrayPrototype::join);
		putMethod(fp, Names.includes, ArrayPrototype::includes);
		putMethod(fp, Names.toString, ArrayPrototype::toStringMethod);
		putMethod(fp, Names.forEach, ArrayPrototype::forEach);
		putMethod(fp, Names.reverse, ArrayPrototype::reverse);
		putMethod(fp, Names.pop, ArrayPrototype::pop);

		final NativeFunction values = putMethod(fp, Names.values, ArrayPrototype::values);
		put(SymbolValue.iterator, values);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.includes")
	private static BooleanValue includes(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.14 Array.prototype.includes ( searchElement [ , fromIndex ] )
		final Value<?> searchElement = argument(0, arguments);
		final Value<?> fromIndex = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If len is 0, return false.
		if (len == 0) return BooleanValue.FALSE;
		// 4. Let n be ? ToIntegerOrInfinity(fromIndex).
		int n = NumberPrototype.toIntegerOrInfinity(interpreter, fromIndex);
		// 5. Assert: If fromIndex is undefined, then n is 0.
		assert fromIndex != Undefined.instance || n == 0;
		// 6. If n is +∞, return false.
		if (n == Integer.MAX_VALUE) return BooleanValue.FALSE;
			// 7. Else if n is -∞, set n to 0.
		else if (n == Integer.MIN_VALUE) n = 0;
		// 8. If n ≥ 0, then
		long k;
		if (n >= 0) {
			// a. Let k be n.
			k = n;
		}
		// 9. Else,
		else {
			// a. Let k be len + n.
			k = len + n;
			// b. If k < 0, set k to 0.
			if (k < 0) k = 0;
		}
		// 10. Repeat, while k < len,
		while (k < len) {
			// a. Let elementK be ? Get(O, ! ToString(𝔽(k))).
			final Value<?> elementK = O.get(interpreter, new StringValue(k));
			// b. If SameValueZero(searchElement, elementK) is true, return true.
			if (searchElement.sameValueZero(elementK)) return BooleanValue.TRUE;
			// c. Set k to k + 1.
			k = k + 1;
		}
		// 11. Return false.
		return BooleanValue.FALSE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.pop")
	private static Value<?> pop(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.20 Array.prototype.pop ( )

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. If len = 0, then
		if (len == 0) {
			// a. Perform ? Set(O, "length", +0𝔽, true).
			O.set(interpreter, Names.length, NumberValue.ZERO);
			// b. Return undefined.
			return Undefined.instance;
		}
		// 4. Else,
		else {
			// a. Assert: len > 0.
			// b. Let newLen be 𝔽(len - 1).
			final long newLen = len - 1;
			// c. Let index be ! ToString(newLen).
			final StringValue index = new StringValue(newLen);
			// d. Let element be ? Get(O, index).
			final Value<?> element = O.get(interpreter, index);
			// e. Perform ? DeletePropertyOrThrow(O, index).
			O.deletePropertyOrThrow(interpreter, index);
			// f. Perform ? Set(O, "length", newLen, true).
			O.set(interpreter, Names.length, new NumberValue(newLen));
			// g. Return element.
			return element;
		}
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
		final Executable executable = Executable.getExecutable(interpreter, callbackfn);
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
		return new ArrayObject(interpreter, Arrays.copyOfRange(A, 0, to));
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
		final Executable executable = Executable.getExecutable(interpreter, callbackfn);
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
		final Executable f_Func = func instanceof Executable e ? e : interpreter.intrinsics.objectPrototype.toStringMethod;
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
			throw AbruptCompletion.error(new TypeError(interpreter, "Pushing " + argCount + " elements on an array-like of length " + len + " is disallowed, as the total surpasses 2^53-1"));
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
		final Executable executable = Executable.getExecutable(interpreter, callbackfn);

		final Value<?>[] values = new Value<?>[(int) len];
		for (int k = 0; k < len; k++) {
			final var Pk = new StringValue(k);
			if (O.hasOwnProperty(Pk)) {
				values[k] = executable.call(interpreter, thisArg, O.get(interpreter, Pk), new NumberValue(k), O);
			}
		}

		return new ArrayObject(interpreter, values);
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
		final Executable callback = Executable.getExecutable(interpreter, callbackfn);
		// 4. If len = 0 and initialValue is not present, throw a TypeError exception.
		if (len == 0 && arguments.length == 1) throw AbruptCompletion.error(new TypeError(interpreter, "Reduce of empty array with no initial value"));

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
			if (!kPresent) throw AbruptCompletion.error(new TypeError(interpreter, "Reduce of empty array with no initial value"));
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.reverse")
	// TODO: Non-mutating Array.prototype.reverse*d*
	private static ObjectValue reverse(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.24 Array.prototype.reverse ( )
		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(O, interpreter);
		// 3. Let middle be floor(len / 2).
		final long middle = Math.floorDiv(len, 2);
		// 4. Let lower be 0.
		int lower = 0;
		// 5. Repeat, while lower ≠ middle,
		while (lower != middle) {
			// a. Let upper be len - lower - 1.
			final long upper = len - lower - 1;
			// b. Let upperP be ! ToString(𝔽(upper)).
			final StringValue upperP = new StringValue(upper);
			// c. Let lowerP be ! ToString(𝔽(lower)).
			final StringValue lowerP = new StringValue(lower);
			// d. Let lowerExists be ? HasProperty(O, lowerP).
			final boolean lowerExists = O.hasProperty(lowerP);
			// e. If lowerExists is true, then
			Value<?> lowerValue = Undefined.instance;
			if (lowerExists) {
				// i. Let lowerValue be ? Get(O, lowerP).
				lowerValue = O.get(interpreter, lowerP);
			}

			// f. Let upperExists be ? HasProperty(O, upperP).
			final boolean upperExists = O.hasProperty(upperP);
			// g. If upperExists is true, then
			Value<?> upperValue = Undefined.instance;
			if (upperExists) {
				// i. Let upperValue be ? Get(O, upperP).
				upperValue = O.get(interpreter, upperP);
			}

			// h. If lowerExists is true and upperExists is true, then
			if (lowerExists && upperExists) {
				// i. Perform ? Set(O, lowerP, upperValue, true).
				O.set(interpreter, lowerP, upperValue /* FIXME: true */);
				// ii. Perform ? Set(O, upperP, lowerValue, true).
				O.set(interpreter, upperP, lowerValue /* FIXME: true */);
			}
			// i. Else if lowerExists is false and upperExists is true, then
			else if (!lowerExists && upperExists) {
				// i. Perform ? Set(O, lowerP, upperValue, true).
				O.set(interpreter, lowerP, upperValue /* FIXME: true */);
				// ii. Perform ? DeletePropertyOrThrow(O, upperP).
				O.deletePropertyOrThrow(interpreter, upperP);

			}
			// j. Else if lowerExists is true and upperExists is false, then
			else if (lowerExists && !upperExists) {
				// i. Perform ? DeletePropertyOrThrow(O, lowerP).
				O.deletePropertyOrThrow(interpreter, lowerP);
				// ii. Perform ? Set(O, upperP, lowerValue, true).
				O.set(interpreter, upperP, lowerValue /* FIXME: true */);
			}
			// k. Else,
			// i. Assert: lowerExists and upperExists are both false.
			// ii. No action is required.
			// l. Set lower to lower + 1.
			lower = lower + 1;
		}

		// 6. Return O.
		return O;
	}

	private static final class ArrayIterator extends ObjectValue {
		private final ObjectValue O;
		private final long len;
		private int index;

		public ArrayIterator(Interpreter interpreter) throws AbruptCompletion {
			super(null);
			this.O = interpreter.thisValue().toObjectValue(interpreter);
			this.len = lengthOfArrayLike(O, interpreter);
			this.index = 0;
			this.putMethod(interpreter.intrinsics.functionPrototype, Names.next, this::next);
		}

		private Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			final ObjectValue result = new ObjectValue(interpreter.intrinsics.objectPrototype);
			result.put(Names.value, index > len ? Undefined.instance : O.get(interpreter, new StringValue(index++)));
			result.put(Names.done, BooleanValue.of(index > len));
			return result;
		}
	}
}