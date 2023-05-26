package xyz.lebster.core.value.array;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Constructor;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.IteratorHelper.iteratorValue;
import static xyz.lebster.core.value.array.ArrayPrototype.lengthOfArrayLike;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.function.NativeFunction.argumentInt;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-constructor")
public class ArrayConstructor extends BuiltinConstructor<ArrayObject, ArrayPrototype> {
	public ArrayConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Array, 1);
		putMethod(intrinsics, Names.of, 2, ArrayConstructor::of);
		putMethod(intrinsics, Names.from, 1, ArrayConstructor::from);
		putMethod(intrinsics, Names.isArray, 1, ArrayConstructor::isArray);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.from")
	private static ObjectValue from(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 23.1.2.1 Array.from ( items [ , mapfn [ , thisArg ] ] )
		final Value<?> items = argument(0, arguments);
		final Value<?> potential_mapfn = argument(1, arguments);
		final Value<?> thisArg = argument(2, arguments);

		// 1. Let C be the `this` value.
		final Value<?> C = interpreter.thisValue();
		// 2. If mapfn is undefined, let mapping be false.
		boolean mapping;
		Executable mapFn = null;
		if (potential_mapfn == Undefined.instance) mapping = false;
			// 3. Else,
		else {
			// a. If IsCallable(mapfn) is false, throw a TypeError exception.
			mapFn = Executable.getExecutable(interpreter, potential_mapfn);
			// b. Let mapping be true.
			mapping = true;
		}

		// 4. Let usingIterator be ? GetMethod(items, @@iterator).
		final var usingIterator = items.toObjectValue(interpreter).getMethod(interpreter, SymbolValue.iterator);
		// 5. If usingIterator is not undefined, then
		if (usingIterator != null) {
			// a. If IsConstructor(C) is true, then
			ObjectValue A = C instanceof final Constructor constructor ?
				// i. Let A be ? Construct(C).
				constructor.construct(interpreter, new Value[0], constructor) :
				// b. Else, i. Let A be ! ArrayCreate(0).
				arrayCreate(interpreter, 0, interpreter.intrinsics.arrayPrototype);

			// c. Let iteratorRecord be ? GetIterator(items, sync, usingIterator).
			final var iteratorRecord = IteratorHelper.getIterator(interpreter, items);
			// d. Let k be 0.
			long k = 0;
			// e. Repeat,
			while (true) {
				// i. If k ≥ 2^53 - 1, then
				if (k > ArrayPrototype.MAX_LENGTH) {
					// 1. Let error be ThrowCompletion(a newly created TypeError object).
					final var error = AbruptCompletion.error(new TypeError(interpreter, "Maximum array size exceeded"));
					// FIXME: 2. Return ? IteratorClose(iteratorRecord, error).
					throw error;
				}

				// ii. Let Pk be ! ToString(𝔽(k)).
				final var Pk = new StringValue(k);
				// iii. Let next be ? IteratorStep(iteratorRecord).
				final ObjectValue next = iteratorRecord.step(interpreter);
				// iv. If next is false, then
				if (next == null) {
					// 1. Perform ? Set(A, "length", 𝔽(k), true).
					A.set(interpreter, Names.length, new NumberValue(k) /* FIXME: , true */);
					// 2. Return A.
					return A;
				}

				// v. Let nextValue be ? IteratorValue(next).
				final var nextValue = iteratorValue(interpreter, next);
				// vi. If mapping is true, then
				final Value<?> mappedValue = mapping ?
					// FIXME: 1. Let mappedValue be Completion(Call(mapfn, thisArg, « nextValue, 𝔽(k) »)).
					// FIXME: 2. IfAbruptCloseIterator(mappedValue, iteratorRecord).
					mapFn.call(interpreter, thisArg, nextValue, new NumberValue(k)) :
					// vii. Else, let mappedValue be nextValue.
					nextValue;

				// FIXME: viii. Let defineStatus be Completion(CreateDataPropertyOrThrow(A, Pk, mappedValue)).
				// FIXME: ix. IfAbruptCloseIterator(defineStatus, iteratorRecord).

				A.set(interpreter, Pk, mappedValue);
				// x. Set k to k + 1.
				k = k + 1;
			}
		}

		// 6. NOTE: items is not an Iterable so assume it is an array-like object.
		// 7. Let arrayLike be ! ToObject(items).
		final ObjectValue arrayLike = items.toObjectValue(interpreter);
		// 8. Let len be ? LengthOfArrayLike(arrayLike).
		final long len = lengthOfArrayLike(interpreter, arrayLike);
		// 9. If IsConstructor(C) is true, then
		final ObjectValue A = C instanceof final Constructor constructor ?
			// a. Let A be ? Construct(C, « 𝔽(len) »).
			constructor.construct(interpreter, new Value[] { new NumberValue(len) }, constructor) :
			// 10. Else, a. Let A be ? ArrayCreate(len).
			arrayCreate(interpreter, Math.toIntExact(len), interpreter.intrinsics.arrayPrototype);

		// 11. Let k be 0.
		int k = 0;
		// 12. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. Let kValue be ? Get(arrayLike, Pk).
			final Value<?> kValue = arrayLike.get(interpreter, Pk);
			// c. If mapping is true, then
			final Value<?> mappedValue = mapping ?
				// i. Let mappedValue be ? Call(mapfn, thisArg, « kValue, 𝔽(k) »).
				mapFn.call(interpreter, thisArg, kValue, new NumberValue(k)) :
				// d. Else, let mappedValue be kValue.
				kValue;
			// FIXME: e. Perform ? CreateDataPropertyOrThrow(A, Pk, mappedValue).
			A.set(interpreter, Pk, mappedValue);
			// f. Set k to k + 1.
			k = k + 1;
		}

		// 13. Perform ? Set(A, "length", 𝔽(len), true).
		A.set(interpreter, Names.length, new NumberValue(len)/* FIXME: , true */);
		// 14. Return A.
		return A;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isarray")
	private static BooleanValue isArray(Interpreter interpreter, Value<?>[] arguments) {
		// 7.2.2 IsArray ( argument )
		final Value<?> argument = argument(0, arguments);

		return BooleanValue.of(argument instanceof ArrayObject);
	}

	@NonStandard
	private static ArrayObject of(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Array.of<T>(length: number, callbackFn: (index: number) => T, thisArg?: unknown): T[]

		final int len = argumentInt(0, 0, interpreter, arguments);
		final Value<?> callbackFn = argument(1, arguments);
		final Value<?> thisArg = argument(2, arguments);

		final Executable executable = Executable.getExecutable(interpreter, callbackFn);
		final Value<?>[] result = new Value<?>[len];
		for (int k = 0; k < len; k++)
			result[k] = executable.call(interpreter, thisArg, new NumberValue(k));
		return new ArrayObject(interpreter, result);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-arraycreate")
	// FIXME: Make this the source of truth instead of ArrayObject's constructor
	private static ArrayObject arrayCreate(Interpreter interpreter, int length, ArrayPrototype proto) {
		// 10.4.2.2 ArrayCreate ( length [ , proto ] )

		// 1. If length > 2^32 - 1, throw a RangeError exception.
		// 2. If proto is not present, set proto to %Array.prototype%.
		// 3. Let A be MakeBasicObject(« [[Prototype]], [[Extensible]] »).
		// 4. Set A.[[Prototype]] to proto.
		// 5. Set A.[[DefineOwnProperty]] as specified in 10.4.2.1.
		// 6. Perform ! OrdinaryDefineOwnProperty(A, "length", PropertyDescriptor { [[Value]]: 𝔽(length), [[Writable]]: true, [[Enumerable]]: false, [[Configurable]]: false }).
		// 7. Return A.
		return new ArrayObject(interpreter, length);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array")
	public ArrayObject construct(Interpreter interpreter, Value<?>[] values, ObjectValue newTarget) throws AbruptCompletion {
		// 23.1.1.1 Array ( ...values )

		// 1. If NewTarget is undefined, let newTarget be the active function object; else let newTarget be NewTarget.
		// FIXME: 2. Let proto be ? GetPrototypeFromConstructor(newTarget, "%Array.prototype%").
		final ArrayPrototype proto = interpreter.intrinsics.arrayPrototype;
		// 3. Let numberOfArgs be the number of elements in values.
		final int numberOfArgs = values.length;
		// 4. If numberOfArgs = 0, then
		if (numberOfArgs == 0) {
			// a. Return ! ArrayCreate(0, proto).
			return ArrayConstructor.arrayCreate(interpreter, 0, proto);
		}
		// 5. Else if numberOfArgs = 1, then
		else if (numberOfArgs == 1) {
			// a. Let len be values[0].
			final Value<?> len = values[0];
			// b. Let array be ! ArrayCreate(0, proto).
			final ArrayObject array = arrayCreate(interpreter, 0, proto);

			NumberValue intLen;
			if (len instanceof final NumberValue len_number) {
				// i. Let intLen be ! ToUint32(len).
				intLen = new NumberValue(len_number.toUint32());
				// ii. If SameValueZero(intLen, len) is false, throw a RangeError exception.
				if (!NumberValue.sameValueZero(intLen, len_number))
					throw error(new RangeError(interpreter, "Invalid array length"));
			} else {
				// i. Perform ! CreateDataPropertyOrThrow(array, "0", len).
				array.set(interpreter, new StringValue(0), len);
				// ii. Let intLen be 1𝔽.
				intLen = NumberValue.ONE;
			}

			// e. Perform ! Set(array, "length", intLen, true).
			array.set(interpreter, Names.length, intLen /* FIXME: true */);
			// f. Return array.
			return array;
		}
		// 6. Else,
		else {
			// a. Assert: numberOfArgs ≥ 2.
			// b. Let array be ? ArrayCreate(numberOfArgs, proto).
			final ArrayObject array = arrayCreate(interpreter, numberOfArgs, proto);
			// c. Let k be 0.

			// d. Repeat, while k < numberOfArgs,
			for (int k = 0; k < numberOfArgs; k++) {
				// i. Let Pk be ! ToString(𝔽(k)).
				final var Pk = new StringValue(k);
				// ii. Let itemK be values[k].
				// FIXME: iii. Perform ! CreateDataPropertyOrThrow(array, Pk, itemK).
				array.set(interpreter, Pk, values[k]);
			}

			// FIXME: e. Assert: The mathematical value of array's "length" property is numberOfArgs.
			// f. Return array.
			return array;
		}
	}
}
