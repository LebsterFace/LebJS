package xyz.lebster.core.value.set;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.iterator.IteratorObject;
import xyz.lebster.core.value.iterator.IteratorRecord;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayList;
import java.util.Collections;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.iterator.IteratorPrototype.getIteratorFromMethod;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;
import static xyz.lebster.core.value.primitive.number.NumberValue.isNegativeZero;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-set-prototype-object")
public final class SetPrototype extends ObjectValue {
	public SetPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.add, 1, SetPrototype::add);
		putMethod(intrinsics, Names.clear, 0, SetPrototype::clear);
		putMethod(intrinsics, Names.delete, 1, SetPrototype::delete);
		putMethod(intrinsics, Names.difference, 1, SetPrototype::difference);
		putMethod(intrinsics, Names.entries, 0, SetPrototype::entries);
		putMethod(intrinsics, Names.forEach, 1, SetPrototype::forEach);
		putMethod(intrinsics, Names.has, 1, SetPrototype::has);
		putMethod(intrinsics, Names.intersection, 1, SetPrototype::intersection);
		putMethod(intrinsics, Names.isDisjointFrom, 1, SetPrototype::isDisjointFrom);
		putMethod(intrinsics, Names.isSubsetOf, 1, SetPrototype::isSubsetOf);
		putMethod(intrinsics, Names.isSupersetOf, 1, SetPrototype::isSupersetOf);
		putMethod(intrinsics, Names.symmetricDifference, 1, SetPrototype::symmetricDifference);
		putMethod(intrinsics, Names.union, 1, SetPrototype::union);
		putMethod(intrinsics, Names.values, 0, SetPrototype::values);

		final var values = putMethod(intrinsics, Names.values, 0, SetPrototype::values);
		put(SymbolValue.iterator, values); // https://tc39.es/ecma262/multipage#sec-set.prototype-@@iterator
		put(Names.keys, values); // https://tc39.es/ecma262/multipage#sec-set.prototype.keys

		put(SymbolValue.toStringTag, Names.Set, false, false, true);
		putAccessor(intrinsics, Names.size, ($, __) -> requireSetData($, "size").getSize(), null, false, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set-records")
	private record SetRecord(ObjectValue setObject, int size, Executable has, Executable keys) {
	}

	private static SetObject requireSetData(Interpreter interpreter, String methodName) throws AbruptCompletion {
		if (interpreter.thisValue() instanceof final SetObject S) return S;
		throw error(new TypeError(interpreter, "Set.prototype.%s requires that 'this' be a Set.".formatted(methodName)));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getsetrecord")
	private static SetRecord getSetRecord(Interpreter interpreter, Value<?> obj_, String methodName) throws AbruptCompletion {
		// 24.2.1.2 GetSetRecord ( obj )
		// 1. If obj is not an Object, throw a TypeError exception.
		if (!(obj_ instanceof final ObjectValue obj))
			throw error(new TypeError(interpreter, "Set.prototype.%s argument must be an object".formatted(methodName)));
		// 2. Let rawSize be ? Get(obj, "size").
		final Value<?> rawSize = obj.get(interpreter, Names.size);
		// 3. Let numSize be ? ToNumber(rawSize).
		final NumberValue numSize = rawSize.toNumberValue(interpreter);
		// 4. NOTE: If rawSize is undefined, then numSize will be NaN.
		// 5. If numSize is NaN, throw a TypeError exception.
		if (numSize.value.isNaN())
			throw error(new TypeError(interpreter, "The .size property is NaN"));
		// 6. Let intSize be ! ToIntegerOrInfinity(numSize).
		final int intSize = toIntegerOrInfinity(interpreter, numSize);
		// 7. If intSize < 0, throw a RangeError exception.
		if (intSize < 0)
			throw error(new RangeError(interpreter, "The .size property is negative"));
		// 8. Let has be ? Get(obj, "has").
		final Value<?> has_ = obj.get(interpreter, Names.has);
		// 9. If IsCallable(has) is false, throw a TypeError exception.
		final Executable has = Executable.getExecutable(interpreter, has_);
		// 10. Let keys be ? Get(obj, "keys").
		final Value<?> keys_ = obj.get(interpreter, Names.keys);
		// 11. If IsCallable(keys) is false, throw a TypeError exception.
		final Executable keys = Executable.getExecutable(interpreter, keys_);
		// 12. Return a new Set Record { [[SetObject]]: obj, [[Size]]: intSize, [[Has]]: has, [[Keys]]: keys }.
		return new SetRecord(obj, intSize, has, keys);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.add")
	private static Value<?> add(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.1 Set.prototype.add ( value )
		Value<?> value = argument(0, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "add");

		// 3. Let entries be the List that is S.[[SetData]].
		final ArrayList<Value<?>> entries = S.setData;
		// 4. For each element e of entries, do
		for (final Value<?> e : entries) {
			// a. If e is not empty and SameValueZero(e, value) is true, then
			if (e != null && e.sameValueZero(value))
				// i. Return S.
				return S;
		}

		// 5. If value is -0𝔽, set value to +0𝔽.
		if (isNegativeZero(value)) value = NumberValue.ZERO;
		// 6. Append value to entries.
		entries.add(value);
		// 7. Return S.
		return S;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.clear")
	private static Undefined clear(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.2 Set.prototype.clear ( )

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "clear");
		// 3. Let entries be the List that is S.[[SetData]].
		final ArrayList<Value<?>> entries = S.setData;
		// 4. For each element e of entries, do: replace the element of entries whose value is e with an element whose value is empty.
		Collections.fill(entries, null);
		// 5. Return undefined.
		return Undefined.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.delete")
	private static BooleanValue delete(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.4 Set.prototype.delete ( value )
		final Value<?> value = argument(0, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "delete");
		// 3. Let entries be the List that is S.[[SetData]].
		final ArrayList<Value<?>> entries = S.setData;
		// 4. For each element e of entries, do
		for (int i = 0; i < entries.size(); i++) {
			final Value<?> e = entries.get(i);
			// a. If e is not empty and SameValueZero(e, value) is true, then
			if (e != null && e.sameValueZero(value)) {
				// i. Replace the element of entries whose value is e with an element whose value is empty.
				entries.set(i, null);
				// ii. Return true.
				return BooleanValue.TRUE;
			}
		}

		// 5. Return false.
		return BooleanValue.FALSE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.entries")
	private static SetIterator entries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.5 Set.prototype.entries ( )

		// 1. Let S be the `this` value.
		final SetObject S = requireSetData(interpreter, "entries");
		// 2. Return ? CreateSetIterator(S, key+value).
		return new SetIterator(interpreter, S, false);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.foreach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.6 Set.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn_ = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "forEach");
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callbackfn = Executable.getExecutable(interpreter, callbackfn_);
		// 4. Let entries be the List that is S.[[SetData]].
		final ArrayList<Value<?>> entries = S.setData;
		// 5. Let numEntries be the number of elements of entries.
		int numEntries = entries.size();
		// 6. Let index be 0.
		int index = 0;
		// 7. Repeat, while index < numEntries,
		while (index < numEntries) {
			// a. Let e be entries[index].
			final Value<?> e = entries.get(index);
			// b. Set index to index + 1.
			index = index + 1;
			// c. If e is not empty, then
			if (e != null) {
				// i. Perform ? Call(callbackfn, thisArg, « e, e, S »).
				callbackfn.call(interpreter, thisArg, e, e, S);
				// ii. NOTE: The number of elements in entries may have increased during execution of callbackfn.
				// iii. Set numEntries to the number of elements of entries.
				numEntries = entries.size();
			}
		}

		// 8. Return undefined.
		return Undefined.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.has")
	private static Value<?> has(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.7 Set.prototype.has ( value )
		final Value<?> value = argument(0, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "has");
		// 3. Let entries be the List that is S.[[SetData]].
		final ArrayList<Value<?>> entries = S.setData;
		// 4. For each element e of entries, do
		for (final Value<?> e : entries) {
			// a. If e is not empty and SameValueZero(e, value) is true, return true.
			if (e != null && e.sameValueZero(value)) return BooleanValue.TRUE;
		}

		// 5. Return false.
		return BooleanValue.FALSE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.values")
	private static Value<?> values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.10 Set.prototype.values ( )

		// 1. Let S be the `this` value.
		final SetObject S = requireSetData(interpreter, "values");
		// 2. Return ? CreateSetIterator(S, value).
		return new SetIterator(interpreter, S, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-setdataindex")
	private static int setDataIndex(ArrayList<Value<?>> setData, Value<?> value) {
		// 1. Set value to CanonicalizeKeyedCollectionKey(value).
		// 2. Let size be the number of elements in setData.
		// 3. Let index be 0.
		// 4. Repeat, while index < size,
		// a. Let e be setData[index].
		// b. If e is not EMPTY and e is value, then
		// i. Return index.
		// c. Set index to index + 1.
		// 5. Return NOT-FOUND.
		return setData.indexOf(value.canonicalizeKeyedCollectionKey());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-setdatahas")
	private static boolean setDataHas(ArrayList<Value<?>> setData, Value<?> value) {
		// 1. If SetDataIndex(setData, value) is NOT-FOUND, return false.
		// 2. Return true.
		return setData.contains(value.canonicalizeKeyedCollectionKey());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.difference")
	private static SetObject difference(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.5 Set.prototype.difference ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "difference");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other, "difference");
		// 4. Let resultSetData be a copy of O.[[SetData]].
		final ArrayList<Value<?>> resultSetData = new ArrayList<>(O.setData);
		// 5. If SetDataSize(O.[[SetData]]) ≤ otherRec.[[Size]], then
		if (O.size() <= otherRec.size()) {
			// a. Let thisSize be the number of elements in O.[[SetData]].
			final int thisSize = O.setData.size();
			// b. Let index be 0.
			// c. Repeat, while index < thisSize,
			for (int index = 0; index < thisSize; index = index + 1) {
				// i. Let e be resultSetData[index].
				final Value<?> e = resultSetData.get(index);
				// ii. If e is not EMPTY, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[SetObject]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.setObject(), e).isTruthy(interpreter);
					// 2. If inOther is true, then
					if (inOther) {
						// a. Set resultSetData[index] to EMPTY.
						resultSetData.set(index, null);
					}
				}
				// iii. Set index to index + 1.
			}
		}
		// 6. Else,
		else {
			// a. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
			final IteratorRecord keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
			// b. Let next be NOT-STARTED.
			Value<?> next;
			// c. Repeat, while next is not DONE,
			do {
				// i. Set next to ? IteratorStepValue(keysIter).
				next = keysIter.stepValue(interpreter);
				// ii. If next is not DONE, then
				if (next != null) {
					// 1. Set next to CanonicalizeKeyedCollectionKey(next).
					next = next.canonicalizeKeyedCollectionKey();
					// 2. Let valueIndex be SetDataIndex(resultSetData, next).
					final int valueIndex = setDataIndex(resultSetData, next);
					// 3. If valueIndex is not NOT-FOUND, then
					if (valueIndex != -1) {
						// a. Set resultSetData[valueIndex] to EMPTY.
						resultSetData.set(valueIndex, null);
					}
				}

			} while (next != null);
		}

		// 7. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 8. Set result.[[SetData]] to resultSetData.
		// 9. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.intersection")
	private static SetObject intersection(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.9 Set.prototype.intersection ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "intersection");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other, "intersection");
		// 4. Let resultSetData be a new empty List.
		final ArrayList<Value<?>> resultSetData = new ArrayList<>();
		// 5. If SetDataSize(O.[[SetData]]) ≤ otherRec.[[Size]], then
		if (O.size() <= otherRec.size()) {
			// a. Let thisSize be the number of elements in O.[[SetData]].
			int thisSize = O.setData.size();
			// b. Let index be 0.
			int index = 0;
			// c. Repeat, while index < thisSize,
			while (index < thisSize) {
				// i. Let e be O.[[SetData]][index].
				final Value<?> e = O.setData.get(index);
				// ii. Set index to index + 1.
				index++;
				// iii. If e is not EMPTY, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[SetObject]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.setObject(), e).isTruthy(interpreter);
					// 2. If inOther is true, then
					if (inOther) {
						// a. NOTE: It is possible for earlier calls to otherRec.[[Has]] to remove and re-add an element of O.[[SetData]],
						// which can cause the same element to be visited twice during this iteration.
						// b. If SetDataHas(resultSetData, e) is false, then
						if (!setDataHas(resultSetData, e)) {
							// i. Append e to resultSetData.
							resultSetData.add(e);
						}
					}
					// 3. NOTE: The number of elements in O.[[SetData]] may have increased during execution of otherRec.[[Has]].
					// 4. Set thisSize to the number of elements in O.[[SetData]].
					thisSize = O.setData.size();
				}
			}
		}
		// 6. Else,
		else {
			// a. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
			final IteratorRecord keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
			// b. Let next be NOT-STARTED.
			Value<?> next;
			// c. Repeat, while next is not DONE,
			do {
				// i. Set next to ? IteratorStepValue(keysIter).
				next = keysIter.stepValue(interpreter);
				// ii. If next is not DONE, then
				if (next != null) {
					// 1. Set next to CanonicalizeKeyedCollectionKey(next).
					next = next.canonicalizeKeyedCollectionKey();
					// 2. Let inThis be SetDataHas(O.[[SetData]], next).
					final boolean inThis = setDataHas(O.setData, next);
					// 3. If inThis is true, then
					if (inThis) {
						// a. NOTE: Because other is an arbitrary object, it is possible for its
						// "keys" iterator to produce the same value more than once.
						// b. If SetDataHas(resultSetData, next) is false, then
						if (!setDataHas(resultSetData, next)) {
							// i. Append next to resultSetData.
							resultSetData.add(next);
						}
					}
				}
			} while (next != null);
		}

		// 7. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 8. Set result.[[SetData]] to resultSetData.
		// 9. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.isdisjointfrom")
	private static BooleanValue isDisjointFrom(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.10 Set.prototype.isDisjointFrom ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isDisjointFrom");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other, "isDisjointFrom");
		// 4. If SetDataSize(O.[[SetData]]) ≤ otherRec.[[Size]], then
		if (O.size() <= otherRec.size()) {
			// a. Let thisSize be the number of elements in O.[[SetData]].
			int thisSize = O.setData.size();
			// b. Let index be 0.
			int index = 0;
			// c. Repeat, while index < thisSize,
			while (index < thisSize) {
				// i. Let e be O.[[SetData]][index].
				final Value<?> e = O.setData.get(index);
				// ii. Set index to index + 1.
				index++;
				// iii. If e is not EMPTY, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[SetObject]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.setObject(), e).isTruthy(interpreter);
					// 2. If inOther is true, return false.
					if (inOther) return BooleanValue.FALSE;
					// 3. NOTE: The number of elements in O.[[SetData]] may have increased during execution of otherRec.[[Has]].
					// 4. Set thisSize to the number of elements in O.[[SetData]].
					thisSize = O.setData.size();
				}
			}

		}
		// 5. Else,
		else {
			// a. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
			final IteratorRecord keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
			// b. Let next be NOT-STARTED.
			Value<?> next;
			// c. Repeat, while next is not DONE,
			do {
				// i. Set next to ? IteratorStepValue(keysIter).
				next = keysIter.stepValue(interpreter);
				// ii. If next is not DONE, then
				if (next != null) {
					// 1. If SetDataHas(O.[[SetData]], next) is true, then
					if (setDataHas(O.setData, next)) {
						// TODO: a. Perform ? IteratorClose(keysIter, NormalCompletion(UNUSED)).
						// b. Return false.
						return BooleanValue.FALSE;
					}
				}
			} while (next != null);
		}

		// 6. Return true.
		return BooleanValue.TRUE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.issubsetof")
	private static BooleanValue isSubsetOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.11 Set.prototype.isSubsetOf ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isDisjointFrom");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other, "isDisjointFrom");
		// 4. If SetDataSize(O.[[SetData]]) > otherRec.[[Size]], return false.
		if (O.size() > otherRec.size()) return BooleanValue.FALSE;
		// 5. Let thisSize be the number of elements in O.[[SetData]].
		int thisSize = O.setData.size();
		// 6. Let index be 0.
		int index = 0;
		// 7. Repeat, while index < thisSize,
		while (index < thisSize) {
			// a. Let e be O.[[SetData]][index].
			final Value<?> e = O.setData.get(index);
			// b. Set index to index + 1.
			index++;
			// c. If e is not EMPTY, then
			if (e != null) {
				// i. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[SetObject]], « e »)).
				final boolean inOther = otherRec.has().call(interpreter, otherRec.setObject(), e).isTruthy(interpreter);
				// ii. If inOther is false, return false.
				if (!inOther) return BooleanValue.FALSE;
				// iii. NOTE: The number of elements in O.[[SetData]] may have increased during execution of otherRec.[[Has]].
				// iv. Set thisSize to the number of elements in O.[[SetData]].
				thisSize = O.setData.size();
			}
		}

		// 8. Return true.
		return BooleanValue.TRUE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.issupersetof")
	private static BooleanValue isSupersetOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.12 Set.prototype.isSupersetOf ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isSupersetOf");
		// 3. Let otherRec be ? GetSetRecord(other).
		final var otherRec = getSetRecord(interpreter, other, "isSupersetOf");
		// 4. If SetDataSize(O.[[SetData]]) < otherRec.[[Size]], return false.
		if (O.setData.size() < otherRec.size()) return BooleanValue.FALSE;
		// 5. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
		final IteratorRecord keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
		// 6. Let next be NOT-STARTED.
		Value<?> next;
		// 7. Repeat, while next is not DONE,
		do {
			// a. Set next to ? IteratorStepValue(keysIter).
			next = keysIter.stepValue(interpreter);
			// b. If next is not DONE, then
			if (next != null) {
				// i. If SetDataHas(O.[[SetData]], next) is false, then
				if (!setDataHas(O.setData, next)) {
					// TODO: 1. Perform ? IteratorClose(keysIter, NormalCompletion(UNUSED)).
					// 2. Return false.
					return BooleanValue.FALSE;
				}
			}
		} while (next != null);

		// 8. Return true.
		return BooleanValue.TRUE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.symmetricdifference")
	private static SetObject symmetricDifference(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.15 Set.prototype.symmetricDifference ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "symmetricDifference");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other, "symmetricDifference");
		// 4. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
		final IteratorRecord keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
		// 5. Let resultSetData be a copy of O.[[SetData]].
		final ArrayList<Value<?>> resultSetData = new ArrayList<>(O.setData);
		// 6. Let next be NOT-STARTED.
		Value<?> next;
		// 7. Repeat, while next is not DONE,
		do {
			// a. Set next to ? IteratorStepValue(keysIter).
			next = keysIter.stepValue(interpreter);
			// b. If next is not DONE, then
			if (next != null) {
				// i. Set next to CanonicalizeKeyedCollectionKey(next).
				next = next.canonicalizeKeyedCollectionKey();
				// ii. Let resultIndex be SetDataIndex(resultSetData, next).
				final int resultIndex = setDataIndex(resultSetData, next);
				// iii. If resultIndex is NOT-FOUND, let alreadyInResult be false. Otherwise, let alreadyInResult be true.
				final boolean alreadyInResult = resultIndex != -1;
				// iv. If SetDataHas(O.[[SetData]], next) is true, then
				if (setDataHas(O.setData, next)) {
					// 1. If alreadyInResult is true, set resultSetData[resultIndex] to EMPTY.
					if (alreadyInResult) resultSetData.set(resultIndex, null);
				}
				// v. Else,
				else {
					// 1. If alreadyInResult is false, append next to resultSetData.
					if (!alreadyInResult) resultSetData.add(next);
				}
			}

		} while (next != null);

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.union")
	private static SetObject union(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.4.16 Set.prototype.union ( other )
		final Value<?> other = argument(0, arguments);
		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final var O = requireSetData(interpreter, "union");
		// 3. Let otherRec be ? GetSetRecord(other).
		final var otherRec = getSetRecord(interpreter, other, "union");
		// 4. Let keysIter be ? GetIteratorFromMethod(otherRec.[[SetObject]], otherRec.[[Keys]]).
		final var keysIter = getIteratorFromMethod(interpreter, otherRec.setObject(), otherRec.keys());
		// 5. Let resultSetData be a copy of O.[[SetData]].
		final var resultSetData = new ArrayList<>(O.setData);
		// 6. Let next be NOT-STARTED.
		Value<?> next;
		// 7. Repeat, while next is not DONE,
		do {
			// a. Set next to ? IteratorStepValue(keysIter).
			next = keysIter.stepValue(interpreter);
			// b. If next is not DONE, then
			if (next != null) {
				// i. Set next to CanonicalizeKeyedCollectionKey(next).
				next = next.canonicalizeKeyedCollectionKey();
				// ii. If SetDataHas(resultSetData, next) is false, then
				if (!setDataHas(resultSetData, next)) {
					// 1. Append next to resultSetData.
					resultSetData.add(next);
				}
			}
		} while (next != null);

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createsetiterator")
	private static class SetIterator extends IteratorObject {
		private final boolean valuesOnly;
		private final ArrayList<Value<?>> entries;
		private int index;

		private SetIterator(Interpreter interpreter, SetObject set, boolean valuesOnly) {
			super(interpreter.intrinsics);
			this.index = 0;
			this.entries = set.setData;
			this.valuesOnly = valuesOnly;
		}

		@Override
		public Value<?> next(Interpreter interpreter, Value<?>[] arguments) {
			while (true) {
				if (index >= entries.size()) return setCompleted();

				final Value<?> value = entries.get(index);
				index = index + 1;
				if (value == null) continue;
				return valuesOnly ? value : new ArrayObject(interpreter, value, value);
			}
		}
	}
}