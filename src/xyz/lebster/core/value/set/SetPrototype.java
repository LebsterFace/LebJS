package xyz.lebster.core.value.set;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.Proposal;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.IteratorHelper.IteratorRecord;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.AccessorDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayList;
import java.util.Collections;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.IteratorHelper.iteratorValue;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-set-prototype-object")
public final class SetPrototype extends ObjectValue {
	public SetPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.add, 1, SetPrototype::add);
		putMethod(intrinsics, Names.clear, 0, SetPrototype::clear);
		putMethod(intrinsics, Names.delete, 1, SetPrototype::delete);
		putMethod(intrinsics, Names.entries, 0, SetPrototype::entries);
		putMethod(intrinsics, Names.forEach, 1, SetPrototype::forEach);
		putMethod(intrinsics, Names.has, 1, SetPrototype::has);
		putMethod(intrinsics, Names.union, 1, SetPrototype::union);
		putMethod(intrinsics, Names.intersection, 1, SetPrototype::intersection);
		putMethod(intrinsics, Names.difference, 1, SetPrototype::difference);
		putMethod(intrinsics, Names.symmetricDifference, 1, SetPrototype::symmetricDifference);
		putMethod(intrinsics, Names.isSubsetOf, 1, SetPrototype::isSubsetOf);
		putMethod(intrinsics, Names.isSupersetOf, 1, SetPrototype::isSupersetOf);
		putMethod(intrinsics, Names.isDisjointFrom, 1, SetPrototype::isDisjointFrom);

		final var values = putMethod(intrinsics, Names.values, 0, SetPrototype::values);
		put(SymbolValue.iterator, values); // https://tc39.es/ecma262/multipage#sec-set.prototype-@@iterator
		put(Names.keys, values); // https://tc39.es/ecma262/multipage#sec-set.prototype.keys

		put(SymbolValue.toStringTag, Names.Set, false, false, true);
		this.value.put(Names.size, new AccessorDescriptor(
			new NativeFunction(intrinsics, StringValue.EMPTY, SetPrototype::getSize, 0),
			null,
			false,
			true
		));
	}

	private static SetObject requireSetData(Interpreter interpreter, String methodName) throws AbruptCompletion {
		if (interpreter.thisValue() instanceof final SetObject S) return S;
		throw error(new TypeError(interpreter, "Set.prototype.%s requires that 'this' be a Set.".formatted(methodName)));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-set.prototype.size")
	public static NumberValue getSize(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		return requireSetData(interpreter, "size").getSize();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.add")
	private static Value<?> add(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.1 Set.prototype.add ( value )
		Value<?> value = argument(0, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "add()");

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
		if (NumberValue.isNegativeZero(value)) value = NumberValue.ZERO;
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
		final SetObject S = requireSetData(interpreter, "clear()");
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
		final SetObject S = requireSetData(interpreter, "delete()");
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
	private static Value<?> entries(Interpreter interpreter, Value<?>[] arguments) {
		// 24.2.3.5 Set.prototype.entries ( )

		throw new NotImplemented("Set.prototype.entries");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.foreach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.6 Set.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn_ = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let S be the `this` value.
		// 2. Perform ? RequireInternalSlot(S, [[SetData]]).
		final SetObject S = requireSetData(interpreter, "forEach()");
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
		final SetObject S = requireSetData(interpreter, "has()");
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

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createsetiterator")
	private static Generator createSetIterator(Interpreter interpreter, SetObject set, SetIteratorKind kind) throws AbruptCompletion {
		return new SetIterator(interpreter, set, kind);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set.prototype.values")
	private static Value<?> values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.2.3.10 Set.prototype.values ( )

		// 1. Let S be the `this` value.
		final SetObject S = requireSetData(interpreter, "values()");
		// 2. Return ? CreateSetIterator(S, value).
		return createSetIterator(interpreter, S, SetIteratorKind.Value);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.union")
	public static SetObject union(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1 Set.prototype.union ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "union()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let keysIter be ? GetKeysIterator(otherRec).
		final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
		// 5. Let resultSetData be a copy of O.[[SetData]].
		final ArrayList<Value<?>> resultSetData = new ArrayList<>(O.setData);
		// 6. Let next be true.
		// 7. Repeat, while next is not false,
		ObjectValue next;
		do {
			// a. Set next to ? IteratorStep(keysIter).
			next = keysIter.step(interpreter);
			// b. If next is not false, then
			if (next != null) {
				// i. Let nextValue be ? IteratorValue(next).
				Value<?> nextValue = iteratorValue(interpreter, next);
				// ii. If nextValue is -0𝔽, set nextValue to +0𝔽.
				if (NumberValue.isNegativeZero(nextValue)) nextValue = NumberValue.ZERO;
				// iii. If SetDataHas(resultSetData, nextValue) is false, then
				if (!setDataHas(resultSetData, nextValue)) {
					// 1. Append nextValue to resultSetData.
					resultSetData.add(nextValue);
				}
			}
		} while (next != null);

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-setdatahas")
	private static boolean setDataHas(ArrayList<Value<?>> resultSetData, Value<?> value) {
		// 1. For each element e of resultSetData, do
		for (final Value<?> e : resultSetData) {
			// a. If e is not empty and SameValueZero(e, value) is true, return true.
			if (e != null && e.sameValueZero(value)) return true;
		}

		// 2. Return false.
		return false;
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set-records")
	private record SetRecord(ObjectValue set, int size, Executable has, Executable keys) {
		@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-getkeysiterator")
		private IteratorRecord getKeysIterator(Interpreter interpreter) throws AbruptCompletion {
			// 1. Let keysIter be ? Call(setRec.[[Keys]], setRec.[[Set]]).
			final Value<?> keysIter_ = this.keys.call(interpreter, set());
			// 2. If keysIter is not an Object, throw a TypeError exception.
			if (!(keysIter_ instanceof final ObjectValue keysIter))
				throw error(new TypeError(interpreter, "SetRec.keys() did not return an object"));
			// 3. Let nextMethod be ? Get(keysIter, "next").
			final Value<?> nextMethod_ = keysIter.get(interpreter, Names.next);
			// 4. If IsCallable(nextMethod) is false, throw a TypeError exception.
			final Executable nextMethod = Executable.getExecutable(interpreter, nextMethod_);
			// 5. Return a new Iterator Record { [[Iterator]]: keysIter, [[NextMethod]]: nextMethod, [[Done]]: false }.
			return new IteratorRecord(keysIter, nextMethod, ANSI.stripFormatting(keysIter.toDisplayString()), "keys");
		}
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-getsetrecord")
	private static SetRecord getSetRecord(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		// 1. If obj is not an Object, throw a TypeError exception.
		if (!(value instanceof final ObjectValue obj)) {
			throw error(new TypeError(interpreter, "Not an object"));
		}

		// 2. Let rawSize be ? Get(obj, "size").
		final Value<?> rawSize = obj.get(interpreter, Names.size);
		// 3. Let numSize be ? ToNumber(rawSize).
		final NumberValue numSize = rawSize.toNumberValue(interpreter);
		// 4. NOTE: If rawSize is undefined, then numSize will be NaN.
		// 5. If numSize is NaN, throw a TypeError exception.
		if (numSize.value.isNaN())
			throw error(new TypeError(interpreter, "Size must not be NaN"));
		// 6. Let intSize be ! ToIntegerOrInfinity(numSize).
		final int intSize = toIntegerOrInfinity(interpreter, numSize);
		// 7. Let has be ? Get(obj, "has").
		final Value<?> has_ = obj.get(interpreter, Names.has);
		// 8. If IsCallable(has) is false, throw a TypeError exception.
		final Executable has = Executable.getExecutable(interpreter, has_);
		// 9. Let keys be ? Get(obj, "keys").
		final Value<?> keys_ = obj.get(interpreter, Names.keys);
		// 10. If IsCallable(keys) is false, throw a TypeError exception.
		final Executable keys = Executable.getExecutable(interpreter, keys_);
		// 11. Return a new Set Record { [[Set]]: obj, [[Size]]: intSize, [[Has]]: has, [[Keys]]: keys }.
		return new SetRecord(obj, intSize, has, keys);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.intersection")
	public static SetObject intersection(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 2 Set.prototype.intersection ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "intersection()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let resultSetData be a new empty List.
		final ArrayList<Value<?>> resultSetData = new ArrayList<>();
		// 5. Let thisSize be the number of elements in O.[[SetData]].
		final int thisSize = O.setData.size();
		// 6. If thisSize ≤ otherRec.[[Size]], then
		if (thisSize <= otherRec.size()) {
			// a. For each element e of O.[[SetData]], do
			for (final Value<?> e : O.setData) {
				// i. If e is not empty, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[Set]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.set, e).isTruthy(interpreter);
					// 2. If inOther is true, then
					if (inOther) {
						// a. Append e to resultSetData.
						resultSetData.add(e);
					}
				}
			}
		}
		// 7. Else,
		else {
			// a. Let keysIter be ? GetKeysIterator(otherRec).
			final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
			// b. Let next be true.
			// c. Repeat, while next is not false,
			ObjectValue next;
			do {
				// i. Set next to ? IteratorStep(keysIter).
				next = keysIter.step(interpreter);
				// ii. If next is not false, then
				if (next != null) {
					// 1. Let nextValue be ? IteratorValue(next).
					Value<?> nextValue = iteratorValue(interpreter, next);
					// 2. If nextValue is -0𝔽, set nextValue to +0𝔽.
					if (NumberValue.isNegativeZero(nextValue)) nextValue = NumberValue.ZERO;
					// 3. NOTE: Because other is an arbitrary object, it is possible for its "keys" iterator to produce the same value more than once.
					// 4. Let alreadyInResult be SetDataHas(resultSetData, nextValue).
					final boolean alreadyInResult = setDataHas(resultSetData, nextValue);
					// 5. Let inThis be SetDataHas(O.[[SetData]], nextValue).
					final boolean inThis = setDataHas(O.setData, nextValue);
					// 6. If alreadyInResult is false and inThis is true, then
					if (!alreadyInResult && inThis) {
						// a. Append nextValue to resultSetData.
						resultSetData.add(nextValue);
					}
				}
			} while (next != null);
			// d. NOTE: It is possible for resultSetData not to be a subset of O.[[SetData]] at this point because
			// arbitrary code may have been executed by the iterator, including code which modifies O.[[SetData]].
			// e. Sort the elements of resultSetData so that
			resultSetData.sort((Value<?> a, Value<?> b) -> {
				// all elements which are also in O.[[SetData]] are ordered as they are in O.[[SetData]],
				if (O.setData.contains(a)) return -1;
				// and any additional elements are moved to the end of the list in the same order as they were before sorting resultSetData.
				return 1;
			});
		}

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.difference")
	public static SetObject difference(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3 Set.prototype.difference ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "difference()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let resultSetData be a copy of O.[[SetData]].
		final ArrayList<Value<?>> resultSetData = new ArrayList<>(O.setData);
		// 5. Let thisSize be the number of elements in O.[[SetData]].
		final int thisSize = O.setData.size();
		// 6. If thisSize ≤ otherRec.[[Size]], then
		if (thisSize <= otherRec.size()) {
			// a. For each element e of resultSetData, do
			for (int i = resultSetData.size() - 1; i >= 0; i--) {
				final Value<?> e = resultSetData.get(i);
				// i. If e is not empty, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[Set]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.set(), e).isTruthy(interpreter);
					// 2. If inOther is true, then
					if (inOther) {
						// a. Remove e from resultSetData.
						resultSetData.remove(i);
					}
				}
			}
		}
		// 7. Else,
		else {
			// a. Let keysIter be ? GetKeysIterator(otherRec).
			final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
			// b. Let next be true.
			ObjectValue next;
			// c. Repeat, while next is not false,
			do {
				// i. Set next to ? IteratorStep(keysIter).
				next = keysIter.step(interpreter);
				// ii. If next is not false, then
				if (next != null) {
					// 1. Let nextValue be ? IteratorValue(next).
					Value<?> nextValue = iteratorValue(interpreter, next);
					// 2. If nextValue is -0𝔽, set nextValue to +0𝔽.
					if (NumberValue.isNegativeZero(nextValue)) nextValue = NumberValue.ZERO;
					// 3. If SetDataHas(resultSetData, nextValue) is true, then
					if (setDataHas(resultSetData, nextValue)) {
						// a. Remove nextValue from resultSetData.
						resultSetData.remove(nextValue);
					}
				}
			} while (next != null);
		}

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.symmetricdifference")
	public static SetObject symmetricDifference(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 4 Set.prototype.symmetricDifference ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "symmetricDifference()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let keysIter be ? GetKeysIterator(otherRec).
		final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
		// 5. Let resultSetData be a copy of O.[[SetData]].
		final ArrayList<Value<?>> resultSetData = new ArrayList<>(O.setData);
		// 6. Let next be true.
		ObjectValue next;
		// 7. Repeat, while next is not false,
		do {
			// a. Set next to ? IteratorStep(keysIter).
			next = keysIter.step(interpreter);
			// b. If next is not false, then
			if (next != null) {
				// i. Let nextValue be ? IteratorValue(next).
				Value<?> nextValue = iteratorValue(interpreter, next);
				// ii. If nextValue is -0𝔽, set nextValue to +0𝔽.
				if (NumberValue.isNegativeZero(nextValue)) nextValue = NumberValue.ZERO;
				// iii. Let inResult be SetDataHas(resultSetData, nextValue).
				final boolean inResult = setDataHas(resultSetData, nextValue);
				// iv. If SetDataHas(O.[[SetData]], nextValue) is true, then
				if (setDataHas(O.setData, nextValue)) {
					// 1. If inResult is true, remove nextValue from resultSetData.
					if (inResult) resultSetData.remove(nextValue);
				}
				// v. Else,
				else {
					// 1. If inResult is false, append nextValue to resultSetData.
					if (!inResult) resultSetData.add(nextValue);
				}
			}
		} while (next != null);

		// 8. Let result be OrdinaryObjectCreate(%Set.prototype%, « [[SetData]] »).
		// 9. Set result.[[SetData]] to resultSetData.
		// 10. Return result.
		return new SetObject(interpreter.intrinsics, resultSetData);
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.issubsetof")
	public static BooleanValue isSubsetOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 5 Set.prototype.isSubsetOf ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isSubsetOf()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let thisSize be the number of elements in O.[[SetData]].
		final int thisSize = O.setData.size();
		// 5. If thisSize > otherRec.[[Size]], return false.
		if (thisSize > otherRec.size()) return BooleanValue.FALSE;
		// 6. For each element e of O.[[SetData]], do
		for (final Value<?> e : O.setData) {
			// a. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[Set]], « e »)).
			final boolean inOther = otherRec.has().call(interpreter, otherRec.set(), e).isTruthy(interpreter);
			// b. If inOther is false, return false.
			if (!inOther) return BooleanValue.FALSE;
		}

		// 7. Return true.
		return BooleanValue.TRUE;
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.issupersetof")
	public static BooleanValue isSupersetOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 6 Set.prototype.isSupersetOf ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isSupersetOf()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let thisSize be the number of elements in O.[[SetData]].
		final int thisSize = O.setData.size();
		// 5. If thisSize < otherRec.[[Size]], return false.
		if (thisSize < otherRec.size()) return BooleanValue.FALSE;
		// 6. Let keysIter be ? GetKeysIterator(otherRec).
		final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
		// 7. Let next be true.
		ObjectValue next;
		// 8. Repeat, while next is not false,
		do {
			// a. Set next to ? IteratorStep(keysIter).
			next = keysIter.step(interpreter);
			// b. If next is not false, then
			if (next != null) {
				// i. Let nextValue be ? IteratorValue(next).
				final Value<?> nextValue = iteratorValue(interpreter, next);
				// ii. If SetDataHas(O.[[SetData]], nextValue) is false, return false.
				if (!setDataHas(O.setData, nextValue)) return BooleanValue.FALSE;
			}
		} while (next != null);

		// 9. Return true.
		return BooleanValue.TRUE;
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.isdisjointfrom")
	public static BooleanValue isDisjointFrom(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 7 Set.prototype.isDisjointFrom ( other )
		final Value<?> other = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. Perform ? RequireInternalSlot(O, [[SetData]]).
		final SetObject O = requireSetData(interpreter, "isDisjointFrom()");
		// 3. Let otherRec be ? GetSetRecord(other).
		final SetRecord otherRec = getSetRecord(interpreter, other);
		// 4. Let thisSize be the number of elements in O.[[SetData]].
		final int thisSize = O.setData.size();
		// 5. If thisSize ≤ otherRec.[[Size]], then
		if (thisSize <= otherRec.size()) {
			// a. For each element e of O.[[SetData]], do
			for (final Value<?> e : O.setData) {
				// i. If e is not empty, then
				if (e != null) {
					// 1. Let inOther be ToBoolean(? Call(otherRec.[[Has]], otherRec.[[Set]], « e »)).
					final boolean inOther = otherRec.has().call(interpreter, otherRec.set(), e).isTruthy(interpreter);
					// 2. If inOther is true, return false.
					if (inOther) return BooleanValue.FALSE;
				}
			}
		}
		// 6. Else,
		else {
			// a. Let keysIter be ? GetKeysIterator(otherRec).
			final IteratorRecord keysIter = otherRec.getKeysIterator(interpreter);
			// b. Let next be true.
			ObjectValue next;
			// c. Repeat, while next is not false,
			do {
				// i. Set next to ? IteratorStep(keysIter).
				next = keysIter.step(interpreter);
				// ii. If next is not false, then
				if (next != null) {
					// 1. Let nextValue be ? IteratorValue(next).
					final Value<?> nextValue = iteratorValue(interpreter, next);
					// 2. If SetDataHas(O.[[SetData]], nextValue) is true, return false.
					if (setDataHas(O.setData, nextValue)) return BooleanValue.FALSE;
				}
			} while (next != null);
		}

		// 7. Return true.
		return BooleanValue.TRUE;
	}

	private enum SetIteratorKind { KeyValue, Value }

	private static class SetIterator extends Generator {
		private final SetIteratorKind kind;
		private int index;
		private final ArrayList<Value<?>> entries;

		private SetIterator(Interpreter interpreter, SetObject set, SetIteratorKind kind) {
			super(interpreter.intrinsics);
			this.index = 0;
			this.entries = set.setData;
			this.kind = kind;
		}

		@Override
		public ObjectValue next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			final ObjectValue result = new ObjectValue(interpreter.intrinsics);

			while (true) {
				if (index >= entries.size()) {
					result.put(Names.done, BooleanValue.TRUE);
					result.put(Names.value, Undefined.instance);
					return result;
				}

				final Value<?> e = entries.get(index);
				index = index + 1;
				if (e == null) continue;

				final Value<?> value = kind == SetIteratorKind.KeyValue ? new ArrayObject(interpreter, e, e) : e;
				result.put(Names.value, value);
				result.put(Names.done, BooleanValue.FALSE);
				return result;
			}
		}
	}
}