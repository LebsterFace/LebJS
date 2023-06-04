package xyz.lebster.core.value.array;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.Proposal;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.NativeCode;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.*;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;

public final class ArrayPrototype extends ObjectValue {
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	public ArrayPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.at, 1, ArrayPrototype::at);
		putMethod(intrinsics, Names.concat, 1, ArrayPrototype::concat);
		putMethod(intrinsics, Names.copyWithin, 2, ArrayPrototype::copyWithin);
		putMethod(intrinsics, Names.entries, 0, ArrayPrototype::entries);
		putMethod(intrinsics, Names.every, 1, ArrayPrototype::every);
		putMethod(intrinsics, Names.fill, 1, ArrayPrototype::fill);
		putMethod(intrinsics, Names.filter, 1, ArrayPrototype::filter);
		putMethod(intrinsics, Names.find, 1, ArrayPrototype::find);
		putMethod(intrinsics, Names.findIndex, 1, ArrayPrototype::findIndex);
		putMethod(intrinsics, Names.findLast, 1, ArrayPrototype::findLast);
		putMethod(intrinsics, Names.findLastIndex, 1, ArrayPrototype::findLastIndex);
		putMethod(intrinsics, Names.flat, 0, ArrayPrototype::flat);
		putMethod(intrinsics, Names.flatMap, 1, ArrayPrototype::flatMap);
		putMethod(intrinsics, Names.forEach, 1, ArrayPrototype::forEach);
		putMethod(intrinsics, Names.group, 1, ArrayPrototype::group);
		putMethod(intrinsics, Names.groupToMap, 1, ArrayPrototype::groupToMap);
		putMethod(intrinsics, Names.includes, 1, ArrayPrototype::includes);
		putMethod(intrinsics, Names.indexOf, 1, ArrayPrototype::indexOf);
		putMethod(intrinsics, Names.join, 1, ArrayPrototype::join);
		putMethod(intrinsics, Names.keys, 0, ArrayPrototype::keys);
		putMethod(intrinsics, Names.lastIndexOf, 1, ArrayPrototype::lastIndexOf);
		putMethod(intrinsics, Names.map, 1, ArrayPrototype::map);
		putMethod(intrinsics, Names.pop, 0, ArrayPrototype::pop);
		putMethod(intrinsics, Names.push, 1, ArrayPrototype::push);
		putMethod(intrinsics, Names.reduce, 1, ArrayPrototype::reduce);
		putMethod(intrinsics, Names.reduceRight, 1, ArrayPrototype::reduceRight);
		putMethod(intrinsics, Names.reverse, 0, ArrayPrototype::reverse);
		putMethod(intrinsics, Names.shift, 0, ArrayPrototype::shift);
		putMethod(intrinsics, Names.slice, 2, ArrayPrototype::slice);
		putMethod(intrinsics, Names.some, 1, ArrayPrototype::some);
		putMethod(intrinsics, Names.sort, 1, ArrayPrototype::sort);
		putMethod(intrinsics, Names.splice, 2, ArrayPrototype::splice);
		putMethod(intrinsics, Names.toLocaleString, 0, ArrayPrototype::toLocaleString);
		putMethod(intrinsics, Names.toString, 0, ArrayPrototype::toStringMethod);
		putMethod(intrinsics, Names.unshift, 1, ArrayPrototype::unshift);
		final NativeFunction values = putMethod(intrinsics, Names.values, 0, ArrayPrototype::values);
		put(SymbolValue.iterator, values);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.at")
	private static Value<?> at(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.1 Array.prototype.at ( index )
		final Value<?> index = argument(0, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. Let relativeIndex be ? ToIntegerOrInfinity(index).
		final int relativeIndex = toIntegerOrInfinity(interpreter, index);
		// 4. If relativeIndex ≥ 0, then a. Let k be relativeIndex. 5. Else, a. Let k be len + relativeIndex.
		final long k = relativeIndex >= 0 ? relativeIndex : len + relativeIndex;

		// 6. If k < 0 or k ≥ len, return undefined.
		if (k < 0 || k >= len) return Undefined.instance;
		// 7. Return ? Get(O, ! ToString(𝔽(k))).
		return O.get(interpreter, new StringValue(k));
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.concat")
	private static Value<?> concat(Interpreter interpreter, Value<?>[] items) {
		// 23.1.3.2 Array.prototype.concat ( ...items )
		throw new NotImplemented("Array.prototype.concat");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.copywithin")
	private static Value<?> copyWithin(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.4 Array.prototype.copyWithin ( target, start [ , end ] )
		final Value<?> target = argument(0, arguments);
		final Value<?> start = argument(1, arguments);
		final Value<?> end = argument(2, arguments);

		throw new NotImplemented("Array.prototype.copyWithin");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.entries")
	private static Value<?> entries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.5 Array.prototype.entries ( )

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Return CreateArrayIterator(O, key+value).
		return new ArrayIterator(interpreter, O, true, true);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.every")
	private static BooleanValue every(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.6 Array.prototype.every ( callbackfn [ , thisArg ] )
		final Value<?> potential_callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callbackfn = Executable.getExecutable(interpreter, potential_callbackfn);

		// 5. Repeat, while k < len,
		for (int k = 0; k < len; k = k + 1) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final var kValue = O.get(interpreter, Pk);
				// ii. Let testResult be ToBoolean(? Call(callbackfn, thisArg, « kValue, 𝔽(k), O »)).
				final boolean testResult = callbackfn.call(interpreter, thisArg, kValue, new NumberValue(k), O).isTruthy(interpreter);
				// iii. If testResult is false, return false.
				if (!testResult) return BooleanValue.FALSE;
			}
		}

		// 6. Return true.
		return BooleanValue.TRUE;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.fill")
	private static Value<?> fill(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.7 Array.prototype.fill ( value [ , start [ , end ] ] )
		final Value<?> value = argument(0, arguments);
		final Value<?> start = argument(1, arguments);
		final Value<?> end = argument(2, arguments);

		throw new NotImplemented("Array.prototype.fill");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.find")
	private static Value<?> find(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.9 Array.prototype.find ( predicate [ , thisArg ] )
		final Value<?> maybePredicate = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, maybePredicate);
		// 4. Let k be 0.
		// 5. Repeat, while k < len,
		for (int k = 0; k < len; k++) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kValue be ? Get(O, Pk).
			final Value<?> kValue = O.get(interpreter, Pk);
			// c. Let testResult be ToBoolean(? Call(predicate, thisArg, « kValue, 𝔽(k), O »)).
			final boolean testResult = predicate.call(interpreter, thisArg, kValue, new NumberValue(k), O).isTruthy(interpreter);
			// d. If testResult is true, return kValue.
			if (testResult) return kValue;
			// e. Set k to k + 1.
		}

		// 6. Return undefined.
		return Undefined.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.findindex")
	private static NumberValue findIndex(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.10 Array.prototype.findIndex ( predicate [ , thisArg ] )
		final Value<?> maybePredicate = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, maybePredicate);
		// 4. Let k be 0.
		// 5. Repeat, while k < len,
		for (int k = 0; k < len; k++) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kValue be ? Get(O, Pk).
			final Value<?> kValue = O.get(interpreter, Pk);
			// c. Let testResult be ToBoolean(? Call(predicate, thisArg, « kValue, 𝔽(k), O »)).
			final NumberValue index = new NumberValue(k);
			final boolean testResult = predicate.call(interpreter, thisArg, kValue, index, O).isTruthy(interpreter);
			// d. If testResult is true, return 𝔽(k).
			if (testResult) return index;
			// e. Set k to k + 1.
		}

		// 6. Return -1𝔽.
		return NumberValue.MINUS_ONE;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.findlast")
	private static Value<?> findLast(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.11 Array.prototype.findLast ( predicate [ , thisArg ] )
		final Value<?> maybePredicate = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final int len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, maybePredicate);
		// 4. Let k be len - 1.
		// 5. Repeat, while k ≥ 0,
		for (long k = len - 1; k >= 0; k--) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. Let kValue be ? Get(O, Pk).
			final Value<?> kValue = O.get(interpreter, Pk);
			// c. Let testResult be ToBoolean(? Call(predicate, thisArg, « kValue, 𝔽(k), O »)).
			final boolean testResult = predicate.call(interpreter, thisArg, kValue, new NumberValue(k), O).isTruthy(interpreter);
			// d. If testResult is true, return kValue.
			if (testResult) return kValue;
			// e. Set k to k - 1.
		}

		// 6. Return undefined.
		return Undefined.instance;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.findlastindex")
	private static Value<?> findLastIndex(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.12 Array.prototype.findLastIndex ( predicate [ , thisArg ] )
		final Value<?> maybePredicate = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final int len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, maybePredicate);
		// 4. Let k be len - 1.
		// 5. Repeat, while k ≥ 0,
		for (long k = len - 1; k >= 0; k--) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. Let kValue be ? Get(O, Pk).
			final Value<?> kValue = O.get(interpreter, Pk);
			// c. Let testResult be ToBoolean(? Call(predicate, thisArg, « kValue, 𝔽(k), O »)).
			final NumberValue kNum = new NumberValue(k);
			final boolean testResult = predicate.call(interpreter, thisArg, kValue, kNum, O).isTruthy(interpreter);
			// d. If testResult is true, return 𝔽(k).
			if (testResult) return kNum;
			// e. Set k to k - 1.
		}

		// 6. Return -1𝔽.
		return NumberValue.MINUS_ONE;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.flat")
	private static Value<?> flat(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.13 Array.prototype.flat ( [ depth ] )
		final Value<?> depth = argument(0, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let sourceLen be ? LengthOfArrayLike(O).
		final int sourceLen = lengthOfArrayLike(interpreter, O);
		// 3. Let depthNum be 1.
		int depthNum = 1;
		// 4. If depth is not undefined, then
		if (depth != Undefined.instance) {
			// a. Set depthNum to ? ToIntegerOrInfinity(depth).
			depthNum = toIntegerOrInfinity(interpreter, depth);
			// b. If depthNum < 0, set depthNum to 0.
			if (depthNum < 0) depthNum = 0;
		}

		// 5. Let A be ? ArraySpeciesCreate(O, 0).
		final ArrayObject A = new ArrayObject(interpreter);
		// 6. Perform ? FlattenIntoArray(A, O, sourceLen, 0, depthNum).
		flattenIntoArray(interpreter, A, O, sourceLen, 0, depthNum);
		// 7. Return A.
		return A;
	}

	private static int flattenIntoArray(Interpreter interpreter, ArrayObject target, ObjectValue source, int sourceLen, int start, int depth) throws AbruptCompletion {
		return flattenIntoArray(interpreter, target, source, sourceLen, start, depth, null, null);
	}

	private static int flattenIntoArray(Interpreter interpreter, ArrayObject target, ObjectValue source, int sourceLen, int start, int depth, Executable mapperFunction, Value<?> thisArg) throws AbruptCompletion {
		// 1. Assert: If mapperFunction is present, then IsCallable(mapperFunction) is true, thisArg is present, and depth is 1.
		if (mapperFunction != null) {
			assert thisArg != null;
			assert depth == 1;
		}

		// 2. Let targetIndex be start.
		int targetIndex = start;
		// 3. Let sourceIndex be +0𝔽.
		int sourceIndex = 0;
		// 4. Repeat, while ℝ(sourceIndex) < sourceLen,
		while (sourceIndex < sourceLen) {
			// a. Let P be ! ToString(sourceIndex).
			final StringValue P = new StringValue(sourceIndex);
			// b. Let exists be ? HasProperty(source, P).
			final boolean exists = source.hasProperty(P);
			// c. If exists is true, then
			if (exists) {
				// i. Let element be ? Get(source, P).
				Value<?> element = source.get(interpreter, P);
				// ii. If mapperFunction is present, then
				if (mapperFunction != null) {
					// 1. Set element to ? Call(mapperFunction, thisArg, « element, sourceIndex, source »).
					element = mapperFunction.call(interpreter, thisArg, element, new NumberValue(sourceIndex), source);
				}

				// iii. Let shouldFlatten be false.
				boolean shouldFlatten = false;
				// iv. If depth > 0, then
				if (depth > 0) {
					// TODO: 1. Set shouldFlatten to ? IsArray(element).
					shouldFlatten = element instanceof ArrayObject;
				}

				// v. If shouldFlatten is true, then
				if (shouldFlatten) {
					// 1. If depth = +∞, let newDepth be +∞.
					// 2. Else, let newDepth be depth - 1.
					final int newDepth = depth == Integer.MAX_VALUE ? Integer.MAX_VALUE : depth - 1;
					// 3. Let elementLen be ? LengthOfArrayLike(element).
					final ArrayObject arrayElement = (ArrayObject) element;
					final int elementLen = lengthOfArrayLike(interpreter, arrayElement);
					// 4. Set targetIndex to ? FlattenIntoArray(target, element, elementLen, targetIndex, newDepth).
					targetIndex = flattenIntoArray(interpreter, target, arrayElement, elementLen, targetIndex, newDepth);
				}
				// vi. Else,
				else {
					// TODO: 1. If targetIndex ≥ 2^53 - 1, throw a TypeError exception.
					// FIXME: 2. Perform ? CreateDataPropertyOrThrow(target, ! ToString(𝔽(targetIndex)), element).
					target.set(interpreter, new StringValue(targetIndex), element);
					// 3. Set targetIndex to targetIndex + 1.
					targetIndex = targetIndex + 1;
				}
			}

			// d. Set sourceIndex to sourceIndex + 1𝔽.
			sourceIndex += 1;
		}

		// 5. Return targetIndex.
		return targetIndex;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.flatmap")
	private static Value<?> flatMap(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.14 Array.prototype.flatMap ( mapperFunction [ , thisArg ] )
		final Value<?> mapperFunction_ = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let sourceLen be ? LengthOfArrayLike(O).
		final int sourceLen = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(mapperFunction) is false, throw a TypeError exception.
		final Executable mapperFunction = Executable.getExecutable(interpreter, mapperFunction_);
		// 4. Let A be ? ArraySpeciesCreate(O, 0).
		final ArrayObject A = new ArrayObject(interpreter);
		// 5. Perform ? FlattenIntoArray(A, O, sourceLen, 0, 1, mapperFunction, thisArg).
		flattenIntoArray(interpreter, A, O, sourceLen, 0, 1, mapperFunction, thisArg);
		// 6. Return A.
		return A;
	}

	@NonCompliant
	@Proposal
	@SpecificationURL("https://tc39.es/proposal-array-grouping/#sec-array.prototype.grouptomap")
	private static Value<?> groupToMap(Interpreter interpreter, Value<?>[] arguments) {
		// 2.2 Array.prototype.groupToMap ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		throw new NotImplemented("Array.prototype.groupToMap");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.indexof")
	private static NumberValue indexOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.17 Array.prototype.indexOf ( searchElement [ , fromIndex ] )
		final Value<?> searchElement = argument(0, arguments);
		final Value<?> fromIndex = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final int len = lengthOfArrayLike(interpreter, O);
		// 3. If len = 0, return -1𝔽.
		if (len == 0) return NumberValue.MINUS_ONE;
		// 4. Let n be ? ToIntegerOrInfinity(fromIndex).
		int n = toIntegerOrInfinity(interpreter, fromIndex);
		// 5. Assert: If fromIndex is undefined, then n is 0.
		// 6. If n = +∞, return -1𝔽.
		if (n == Integer.MAX_VALUE) return NumberValue.MINUS_ONE;
		// 7. Else if n = -∞, set n to 0.
		else if (n == Integer.MIN_VALUE) n = 0;
		// 8. If n ≥ 0, then
		int k;
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
			final StringValue Pk = new StringValue(k);
			// a. Let kPresent be ? HasProperty(O, ! ToString(𝔽(k))).
			final boolean kPresent = O.hasProperty(Pk);
			// b. If kPresent is true, then
			if (kPresent) {
				// i. Let elementK be ? Get(O, ! ToString(𝔽(k))).
				final Value<?> elementK = O.get(interpreter, Pk);
				// ii. If IsStrictlyEqual(searchElement, elementK) is true, return 𝔽(k).
				if (searchElement.equals(elementK)) return new NumberValue(k);
			}

			// c. Set k to k + 1.
			k = k + 1;
		}

		// 11. Return -1𝔽.
		return NumberValue.MINUS_ONE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.keys")
	private static ArrayIterator keys(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.19 Array.prototype.keys ( )

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Return CreateArrayIterator(O, key).
		return new ArrayIterator(interpreter, O, true, false);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.lastindexof")
	private static NumberValue lastIndexOf(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.20 Array.prototype.lastIndexOf ( searchElement [ , fromIndex ] )
		final Value<?> searchElement = argument(0, arguments);
		final Value<?> fromIndex = argument(1, arguments);

		throw new NotImplemented("Array.prototype.lastIndexOf");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.reduceright")
	private static Value<?> reduceRight(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.25 Array.prototype.reduceRight ( callbackfn [ , initialValue ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> initialValue = argument(1, arguments);

		throw new NotImplemented("Array.prototype.reduceRight");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.shift")
	private static Value<?> shift(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.27 Array.prototype.shift ( )

		throw new NotImplemented("Array.prototype.shift");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.some")
	private static BooleanValue some(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.29 Array.prototype.some ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn_ = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callbackFn = Executable.getExecutable(interpreter, callbackfn_);
		// 4. Let k be 0.
		// 5. Repeat, while k < len,
		for (int k = 0; k < len; k++) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final Value<?> kValue = O.get(interpreter, Pk);
				// ii. Let testResult be ToBoolean(? Call(callbackfn, thisArg, « kValue, 𝔽(k), O »)).
				final boolean testResult = callbackFn.call(interpreter, thisArg, kValue, new NumberValue(k), O).isTruthy(interpreter);
				// iii. If testResult is true, return true.
				if (testResult) return BooleanValue.TRUE;
			}

			// d. Set k to k + 1.
		}

		// 6. Return false.
		return BooleanValue.FALSE;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.sort")
	private static Value<?> sort(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.30 Array.prototype.sort ( comparefn )
		final Value<?> comparefn = argument(0, arguments);

		// 1. If comparefn is not undefined and IsCallable(comparefn) is false, throw a TypeError exception.
		if (comparefn != Undefined.instance && !(comparefn instanceof Executable)) {
			throw Executable.notCallable(interpreter, comparefn);
		}

		// 2. Let obj be ? ToObject(this value).
		final ObjectValue obj = interpreter.thisValue().toObjectValue(interpreter);
		// 3. Let len be ? LengthOfArrayLike(obj).
		final long len = lengthOfArrayLike(interpreter, obj);
		// 4. Let SortCompare be a new Abstract Closure with parameters (x, y) that captures comparefn and performs the following steps when called:
		final NativeCode SortCompare = ($, arguments_) -> {
			final Value<?> x = argument(0, arguments_);
			final Value<?> y = argument(1, arguments_);

			// a. If x and y are both undefined, return +0𝔽.
			if (x == Undefined.instance && y == Undefined.instance) return NumberValue.ZERO;
			// b. If x is undefined, return 1𝔽.
			if (x == Undefined.instance) return NumberValue.ONE;
			// c. If y is undefined, return -1𝔽.
			if (y == Undefined.instance) return NumberValue.MINUS_ONE;
			// d. If comparefn is not undefined, then
			if (comparefn instanceof final Executable executable) {
				// i. Let v be ? ToNumber(? Call(comparefn, undefined, « x, y »)).
				final NumberValue v = executable.call($, Undefined.instance, x, y).toNumberValue($);
				// ii. If v is NaN, return +0𝔽.
				if (v.value.isNaN()) return NumberValue.ZERO;
				// iii. Return v.
				return v;
			}

			// e. Let xString be ? ToString(x).
			final StringValue xString = x.toStringValue($);
			// f. Let yString be ? ToString(y).
			final StringValue yString = y.toStringValue($);
			// g. Let xSmaller be ! IsLessThan(xString, yString, true).
			final boolean xSmaller = isLessThan($, xString, yString, true).value;
			// h. If xSmaller is true, return -1𝔽.
			if (xSmaller) return NumberValue.MINUS_ONE;
			// i. Let ySmaller be ! IsLessThan(yString, xString, true).
			final boolean ySmaller = isLessThan($, yString, xString, true).value;
			// j. If ySmaller is true, return 1𝔽.
			if (ySmaller) return NumberValue.ONE;
			// k. Return +0𝔽.
			return NumberValue.ZERO;
		};

		// 5. Return ? SortIndexedProperties(obj, len, SortCompare).
		return obj.sortIndexedProperties(interpreter, len, SortCompare);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.splice")
	private static Value<?> splice(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.31 Array.prototype.splice ( start, deleteCount, ...items )
		final Value<?> start = argument(0, arguments);
		final Value<?> deleteCount = argument(1, arguments);
		final Value<?>[] items = argumentRest(2, arguments);


		throw new NotImplemented("Array.prototype.splice");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.tolocalestring")
	private static StringValue toLocaleString(Interpreter interpreter, Value<?>[] arguments) {
		// 23.1.3.32 Array.prototype.toLocaleString ( [ reserved1 [ , reserved2 ] ] )
		throw new NotImplemented("Array.prototype.toLocaleString");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.unshift")
	private static NumberValue unshift(Interpreter interpreter, Value<?>[] items) throws AbruptCompletion {
		// 23.1.3.34 Array.prototype.unshift ( ...items )

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. Let argCount be the number of elements in items.
		final int argCount = items.length;
		// 4. If argCount > 0, then
		if (argCount > 0) {
			// a. If len + argCount > 2^53 - 1, throw a TypeError exception.
			if (len + argCount > MAX_LENGTH)
				throw error(new TypeError(interpreter, "Invalid array length"));
			// b. Let k be len.
			long k = len;
			// c. Repeat, while k > 0,
			while (k > 0) {
				// i. Let `from` be ! ToString(𝔽(k - 1)).
				final StringValue from = new StringValue(k - 1);
				// ii. Let `to` be ! ToString(𝔽(k + argCount - 1)).
				final StringValue to = new StringValue(k + argCount - 1);
				// iii. Let fromPresent be ? HasProperty(O, from).
				final boolean fromPresent = O.hasProperty(from);
				// iv. If fromPresent is true, then
				if (fromPresent) {
					// 1. Let fromValue be ? Get(O, from).
					final Value<?> fromValue = O.get(interpreter, from);
					// 2. Perform ? Set(O, to, fromValue, true).
					O.set(interpreter, to, fromValue/* FIXME: , true */);
				}
				// v. Else,
				else {
					// 1. Assert: fromPresent is false.
					// 2. Perform ? DeletePropertyOrThrow(O, to).
					O.deletePropertyOrThrow(interpreter, to);
				}

				// vi. Set k to k - 1.
				k = k - 1;
			}

			// d. Let j be +0𝔽.
			int j = 0;
			// e. For each element E of items, do
			for (final Value<?> E : items) {
				// i. Perform ? Set(O, ! ToString(j), E, true).
				O.set(interpreter, new StringValue(j), E/* FIXME: , true */);
				// ii. Set j to j + 1𝔽.
				j = j + 1;
			}
		}

		// 5. Perform ? Set(O, "length", 𝔽(len + argCount), true).
		O.set(interpreter, Names.length, new NumberValue(len + argCount)/* FIXME: , true */);
		// 6. Return 𝔽(len + argCount).
		return new NumberValue(len + argCount);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.values")
	private static ArrayIterator values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.35 Array.prototype.values ( )

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Return CreateArrayIterator(O, value).
		return new ArrayIterator(interpreter, O, false, true);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-array-grouping/#sec-array.prototype.group")
	private static ObjectValue group(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 2.1 Array.prototype.group ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable executable = Executable.getExecutable(interpreter, callbackfn);
		// 4. Let k be 0.
		int k = 0;
		// 5. Let groups be a new empty List.
		final ArrayList<ArrayGroup> groups = new ArrayList<>();
		// 6. Repeat, while k < len
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kValue be ? Get(O, Pk).
			final Value<?> kValue = O.get(interpreter, Pk);
			// c. Let propertyKey be ? ToPropertyKey(? Call(callbackfn, thisArg, « kValue, 𝔽(k), O »)).
			final Key<?> propertyKey = executable.call(interpreter, thisArg, kValue, new NumberValue(k), O).toPropertyKey(interpreter);
			// d. Perform AddValueToKeyedGroup(groups, propertyKey, kValue).
			addValueToKeyedGroup(groups, propertyKey, kValue);
			// e. Set k to k + 1.
			k = k + 1;
		}

		// 7. Let obj be OrdinaryObjectCreate(null).
		final var obj = new ObjectValue(Null.instance);
		// 8. For each Record { [[Key]], [[Elements]] } g of groups, do
		for (final ArrayGroup g : groups) {
			// a. Let elements be CreateArrayFromList(g.[[Elements]]).
			final var elements = new ArrayObject(interpreter, g.elements);
			// FIXME: b. Perform ! CreateDataPropertyOrThrow(obj, g.[[Key]], elements).
			obj.set(interpreter, g.key, elements);
		}

		// 9. Return obj.
		return obj;
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-array-grouping/#sec-add-value-to-keyed-group")
	private static void addValueToKeyedGroup(ArrayList<ArrayGroup> groups, Key<?> key, Value<?> value) {
		// 2.3 AddValueToKeyedGroup ( groups, key, value )

		// 1. For each Record { [[Key]], [[Elements]] } g of groups, do
		for (final ArrayGroup g : groups) {
			// a. If SameValue(g.[[Key]], key) is true, then
			if (g.key.sameValue(key)) {
				// i. Assert: exactly one element of groups meets this criteria.
				// ii. Append value as the last element of g.[[Elements]].
				g.elements.add(value);
				// iii. Return unused.
				return;
			}
		}

		// 2. Let group be the Record { [[Key]]: key, [[Elements]]: « value » }.
		final var group = new ArrayGroup(key, new ArrayList<>(List.of(value)));
		// 3. Append group as the last element of groups.
		groups.add(group);
		// 4. Return unused.
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.slice")
	private static ArrayObject slice(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.26 Array.prototype.slice ( start, end )
		final Value<?> start = argument(0, arguments);
		final Value<?> end = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. Let relativeStart be ? ToIntegerOrInfinity(start).
		final int relativeStart = toIntegerOrInfinity(interpreter, start);
		long k;
		// 4. If relativeStart is -∞, let k be 0.
		if (relativeStart == Integer.MIN_VALUE) k = 0;
			// 5. Else if relativeStart < 0, let k be max(len + relativeStart, 0).
		else if (relativeStart < 0) k = Math.max(len + relativeStart, 0);
			// 6. Else, let k be min(relativeStart, len).
		else k = Math.min(relativeStart, len);
		// 7. If end is undefined, let relativeEnd be len; else let relativeEnd be ? ToIntegerOrInfinity(end).
		final long relativeEnd = end == Undefined.instance ? len : toIntegerOrInfinity(interpreter, end);
		long final_;
		// 8. If relativeEnd is -∞, let final be 0.
		if (relativeEnd == Integer.MIN_VALUE) final_ = 0;
			// 9. Else if relativeEnd < 0, let final be max(len + relativeEnd, 0).
		else if (relativeEnd < 0) final_ = Math.max(len + relativeEnd, 0);
			// 10. Else, let final be min(relativeEnd, len).
		else final_ = Math.min(relativeEnd, len);
		// 11. Let count be max(final - k, 0).
		final long count = Math.max(final_ - k, 0);
		// FIXME: 12. Let A be ? ArraySpeciesCreate(O, count).
		final Value<?>[] A = new Value<?>[(int) count];
		// 13. Let n be 0.
		int n = 0;
		// 14. Repeat, while k < final,
		while (k < final_) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var PK = new StringValue(k);
			// b. Let kPresent be ? HasProperty(O, Pk).
			final boolean kPresent = O.hasProperty(PK);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(O, Pk).
				final Value<?> kValue = O.get(interpreter, PK);
				// ii. Perform ? CreateDataPropertyOrThrow(A, ! ToString(𝔽(n)), kValue).
				A[n] = kValue;
			}
			// d. Set k to k + 1.
			k = k + 1;
			// e. Set n to n + 1.
			n = n + 1;
		}

		// 15. Perform ? Set(A, "length", 𝔽(n), true).
		// 16. Return A.
		return new ArrayObject(interpreter, A);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.includes")
	private static BooleanValue includes(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.14 Array.prototype.includes ( searchElement [ , fromIndex ] )
		final Value<?> searchElement = argument(0, arguments);
		final Value<?> fromIndex = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If len is 0, return false.
		if (len == 0) return BooleanValue.FALSE;
		// 4. Let n be ? ToIntegerOrInfinity(fromIndex).
		int n = toIntegerOrInfinity(interpreter, fromIndex);
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
		final long len = lengthOfArrayLike(interpreter, O);
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
		final long len = lengthOfArrayLike(interpreter, O);
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.foreach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.3.15 Array.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		final long len = lengthOfArrayLike(interpreter, O);
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
	static int lengthOfArrayLike(Interpreter interpreter, ObjectValue O) throws AbruptCompletion {
		// 1. Return ℝ(? ToLength(? Get(obj, "length"))).
		return toLength(interpreter, O.get(interpreter, Names.length));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tolength")
	private static int toLength(Interpreter interpreter, Value<?> argument) throws AbruptCompletion {
		// 1. Let len be ? ToIntegerOrInfinity(argument).
		final int len = toIntegerOrInfinity(interpreter, argument);
		// 2. If len ≤ 0, return +0𝔽.
		if (len <= 0) return 0;
		// 3. Return 𝔽(min(len, 2^53 - 1)).
		return Math.toIntExact(Math.min(len, MAX_LENGTH));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.push")
	private static NumberValue push(Interpreter interpreter, Value<?>[] items) throws AbruptCompletion {
		// 23.1.3.21 Array.prototype.push ( ...items )
		// 1. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let len be ? LengthOfArrayLike(O).
		long len = lengthOfArrayLike(interpreter, O);
		// 3. Let argCount be the number of elements in items.
		final int argCount = items.length;
		// 4. If len + argCount > 2^53 - 1, throw a TypeError exception.
		if (len + argCount > MAX_LENGTH) {
			final String message = "Pushing %d elements on an array-like of length %d is disallowed, as the total surpasses 2^53-1";
			throw error(new TypeError(interpreter, message.formatted(argCount, len)));
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
		// 23.1.3.21 Array.prototype.map ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(interpreter, O);
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
		final long len = lengthOfArrayLike(interpreter, O);
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callback = Executable.getExecutable(interpreter, callbackfn);
		// 4. If len = 0 and initialValue is not present, throw a TypeError exception.
		if (len == 0 && arguments.length == 1)
			throw error(new TypeError(interpreter, "Reduce of empty array with no initial value"));

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
			if (!kPresent) throw error(new TypeError(interpreter, "Reduce of empty array with no initial value"));
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
		final long len = lengthOfArrayLike(interpreter, O);
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

	private record ArrayGroup(Key<?> key, ArrayList<Value<?>> elements) {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createarrayiterator")
	private static final class ArrayIterator extends Generator {
		private final ObjectValue array;
		private final boolean keys;
		private final boolean values;
		private int index = 0;

		public ArrayIterator(Interpreter interpreter, ObjectValue array, boolean keys, boolean values) throws AbruptCompletion {
			super(interpreter.intrinsics);
			this.array = array;
			this.keys = keys;
			this.values = values;
			if (!keys && !values)
				throw new ShouldNotHappen("Cannot create array iterator of neither keys nor values.");
		}

		@Override
		public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			final int len = lengthOfArrayLike(interpreter, array);
			if (index >= len) {
				setCompleted();
				return Undefined.instance;
			}

			Value<?> result = null;

			final StringValue Pk = new StringValue(index);
			if (keys) result = new NumberValue(index);
			if (values) {
				final Value<?> value = array.get(interpreter, Pk);
				result = keys ? new ArrayObject(interpreter, result, value) : value;
			}

			index += 1;
			return result;
		}
	}
}