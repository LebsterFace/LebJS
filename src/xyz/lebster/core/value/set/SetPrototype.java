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
			final var e = entries.get(index);
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
		final var entries = S.setData;
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
	public static SetObject union(Interpreter interpreter, Value<?>[] arguments) {
		// 1 Set.prototype.union ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.union");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.intersection")
	public static SetObject intersection(Interpreter interpreter, Value<?>[] arguments) {
		// 2 Set.prototype.intersection ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.intersection");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.difference")
	public static SetObject difference(Interpreter interpreter, Value<?>[] arguments) {
		// 3 Set.prototype.difference ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.difference");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.symmetricdifference")
	public static SetObject symmetricDifference(Interpreter interpreter, Value<?>[] arguments) {
		// 4 Set.prototype.symmetricDifference ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.symmetricDifference");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.issubsetof")
	public static BooleanValue isSubsetOf(Interpreter interpreter, Value<?>[] arguments) {
		// 5 Set.prototype.isSubsetOf ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.isSubsetOf");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.issupersetof")
	public static BooleanValue isSupersetOf(Interpreter interpreter, Value<?>[] arguments) {
		// 6 Set.prototype.isSupersetOf ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.isSupersetOf");
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-set-methods/#sec-set.prototype.isdisjointfrom")
	public static BooleanValue isDisjointFrom(Interpreter interpreter, Value<?>[] arguments) {
		// 7 Set.prototype.isDisjointFrom ( other )
		final Value<?> other = argument(0, arguments);

		throw new NotImplemented("Set.prototype.isDisjointFrom");
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