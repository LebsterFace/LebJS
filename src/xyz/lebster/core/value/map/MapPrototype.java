package xyz.lebster.core.value.map;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.map.MapObject.MapEntry;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayList;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.primitive.number.NumberValue.isNegativeZero;

@NonStandard
@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-map-prototype-object")
public final class MapPrototype extends ObjectValue {
	public MapPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.clear, 0, MapPrototype::clear);
		putMethod(intrinsics, Names.delete, 1, MapPrototype::deleteMethod);
		final NativeFunction entries = putMethod(intrinsics, Names.entries, 0, MapPrototype::entries);
		putMethod(intrinsics, Names.forEach, 1, MapPrototype::forEach);
		putMethod(intrinsics, Names.get, 1, MapPrototype::getMethod);
		putMethod(intrinsics, Names.has, 1, MapPrototype::has);
		putMethod(intrinsics, Names.keys, 0, MapPrototype::keys);
		putMethod(intrinsics, Names.set, 2, MapPrototype::setMethod);
		putAccessor(intrinsics, Names.size, MapPrototype::getSize, null, false, true);
		putMethod(intrinsics, Names.values, 0, MapPrototype::values);
		put(SymbolValue.iterator, entries);
		put(SymbolValue.toStringTag, Names.Map, false, false, true);
	}

	private static MapObject requireMapData(Interpreter interpreter, String methodName) throws AbruptCompletion {
		if (interpreter.thisValue() instanceof final MapObject M) return M;
		throw error(new TypeError(interpreter, "Map.prototype.%s requires that 'this' be a Map.".formatted(methodName)));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.clear")
	private static Undefined clear(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.1 Map.prototype.clear ( )

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "clear");
		// 3. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		// a. Set p.[[Key]] to empty.
		// b. Set p.[[Value]] to empty.
		M.mapData.clear();
		// 4. Return undefined.
		return Undefined.instance;
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.delete")
	private static BooleanValue deleteMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.3 Map.prototype.delete ( key )
		final Value<?> key = argument(0, arguments);

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "clear");
		// 3. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (int i = 0; i < M.mapData.size(); i++) {
			MapEntry p = M.mapData.get(i);
			// a. If p.[[Key]] is not empty and SameValueZero(p.[[Key]], key) is true, then
			if (p != null && p.key.sameValueZero(key)) {
				// i. Set p.[[Key]] to empty.
				// ii. Set p.[[Value]] to empty.
				M.mapData.set(i, null);
				// iii. Return true.
				return BooleanValue.TRUE;
			}
		}

		// 4. Return false.
		return BooleanValue.FALSE;
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.entries")
	private static MapIterator entries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.4 Map.prototype.entries ( )

		// 1. Let M be the `this` value.
		final MapObject M = requireMapData(interpreter, "entries");
		// 2. Return ? CreateMapIterator(M, key+value).
		return new MapIterator(interpreter, M, true, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.foreach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.5 Map.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn_ = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "clear");
		// 3. If IsCallable(callbackfn) is false, throw a TypeError exception.
		final Executable callbackfn = Executable.getExecutable(interpreter, callbackfn_);
		// 4. Let entries be M.[[MapData]].
		final ArrayList<MapEntry> entries = M.mapData;
		// 5. Let numEntries be the number of elements in entries.
		// 6. Let index be 0.
		// 7. Repeat, while index < numEntries,
		for (final MapEntry e : entries) {
			// a. Let e be entries[index].
			// b. Set index to index + 1.
			// c. If e.[[Key]] is not empty, then
			if (e != null) {
				// i. Perform ? Call(callbackfn, thisArg, « e.[[Value]], e.[[Key]], M »).
				callbackfn.call(interpreter, thisArg, e.value, e.key, M);
			}

			// ii. NOTE: The number of elements in entries may have increased during execution of callbackfn.
			// iii. Set numEntries to the number of elements in entries.
		}

		// 8. Return undefined.
		return Undefined.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.get")
	private static Value<?> getMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.6 Map.prototype.get ( key )
		final Value<?> key = argument(0, arguments);

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "clear");
		// 3. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (final MapEntry p : M.mapData) {
			// a. If p.[[Key]] is not empty and SameValueZero(p.[[Key]], key) is true, return p.[[Value]].
			if (p.key.sameValueZero(key)) return p.value;
		}

		// 4. Return undefined.
		return Undefined.instance;
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.has")
	private static BooleanValue has(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.7 Map.prototype.has ( key )
		final Value<?> key = argument(0, arguments);

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "clear");
		// 3. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (final MapEntry p : M.mapData) {
			// a. If p.[[Key]] is not empty and SameValueZero(p.[[Key]], key) is true, return true.
			if (p != null && p.key.sameValueZero(key)) return BooleanValue.TRUE;
		}

		// 4. Return false.
		return BooleanValue.FALSE;
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.keys")
	private static MapIterator keys(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.8 Map.prototype.keys ( )

		// 1. Let M be the `this` value.
		final MapObject M = requireMapData(interpreter, "keys");
		// 2. Return ? CreateMapIterator(M, key).
		return new MapIterator(interpreter, M, true, false);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.set")
	private static MapObject setMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.9 Map.prototype.set ( key, value )
		Value<?> key = argument(0, arguments);
		final Value<?> value = argument(1, arguments);

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "set");
		// 3. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (final MapEntry p : M.mapData) {
			// a. If p.[[Key]] is not empty and SameValueZero(p.[[Key]], key) is true, then
			if (p != null && p.key.sameValueZero(key)) {
				// i. Set p.[[Value]] to value.
				p.value = value;
				// ii. Return M.
				return M;
			}
		}

		// 4. If key is -0𝔽, set key to +0𝔽.
		if (isNegativeZero(key)) key = NumberValue.ZERO;
		// 5. Let p be the Record { [[Key]]: key, [[Value]]: value }.
		final MapEntry p = new MapEntry(key, value);
		// 6. Append p to M.[[MapData]].
		M.mapData.add(p);
		// 7. Return M.
		return M;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-map.prototype.size")
	private static NumberValue getSize(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.10 get Map.prototype.size

		// 1. Let M be the `this` value.
		// 2. Perform ? RequireInternalSlot(M, [[MapData]]).
		final MapObject M = requireMapData(interpreter, "size");
		// 3. Let count be 0.
		int count = 0;
		// 4. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (final MapEntry p : M.mapData) {
			// a. If p.[[Key]] is not empty, set count to count + 1.
			if (p != null) count += 1;
		}

		// 5. Return 𝔽(count).
		return new NumberValue(count);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.values")
	private static MapIterator values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.3.11 Map.prototype.values ( )

		// 1. Let M be the `this` value.
		final MapObject M = requireMapData(interpreter, "values");
		// 2. Return ? CreateMapIterator(M, value).
		return new MapIterator(interpreter, M, false, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createmapiterator")
	private static final class MapIterator extends Generator {
		private final ArrayList<MapEntry> mapData;
		private final boolean keys;
		private final boolean values;
		private int index = 0;

		public MapIterator(Interpreter interpreter, MapObject map, boolean keys, boolean values) {
			super(interpreter.intrinsics);
			this.mapData = map.mapData;
			this.keys = keys;
			this.values = values;
			if (!keys && !values)
				throw new ShouldNotHappen("Cannot create map iterator of neither keys nor values.");
		}

		@Override
		public Value<?> next(Interpreter interpreter, Value<?>[] arguments) {
			while (true) {
				if (index >= mapData.size()) {
					setCompleted();
					return Undefined.instance;
				}

				final MapEntry entry = mapData.get(index);
				index += 1;
				if (entry == null) continue;

				if (keys && values) return new ArrayObject(interpreter, entry.key, entry.value);
				if (keys) return entry.key;
				if (values) return entry.value;

				throw new ShouldNotHappen("Map iterator of neither keys nor values");
			}
		}
	}
}