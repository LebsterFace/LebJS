package xyz.lebster.core.value.map;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.map.MapObject.MapEntry;
import xyz.lebster.core.value.object.ObjectConstructor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.iterator.IteratorPrototype.getIterator;
import static xyz.lebster.core.value.iterator.IteratorPrototype.iteratorValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-constructor")
public final class MapConstructor extends BuiltinConstructor<MapObject, MapPrototype> {
	public MapConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Map, 0);
		putMethod(intrinsics, Names.groupBy, 2, MapConstructor::groupBy);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.groupby")
	private static MapObject groupBy(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 24.1.2.1 Map.groupBy ( items, callbackfn )
		final Value<?> items = argument(0, arguments);
		final Value<?> callbackfn = argument(1, arguments);

		// 1. Let groups be ? GroupBy(items, callbackfn, COLLECTION).
		final var groups = ObjectConstructor.groupBy(interpreter, items, callbackfn, false);
		// 2. Let map be ! Construct(%Map%).
		final ArrayList<MapEntry> mapData = new ArrayList<>();
		final MapObject map = new MapObject(interpreter.intrinsics, mapData);
		// 3. For each Record { [[Key]], [[Elements]] } g of groups, do
		for (final var g : groups) {
			// a. Let elements be CreateArrayFromList(g.[[Elements]]).
			final ArrayObject elements = new ArrayObject(interpreter, g.elements());
			// b. Let entry be the Record { [[Key]]: g.[[Key]], [[Value]]: elements }.
			final var entry = new MapEntry(g.key(), elements);
			// c. Append entry to map.[[MapData]].
			mapData.add(entry);
		}

		// 4. Return map.
		return map;
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-iterable")
	public MapObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		// 24.1.1.1 Map ( [ iterable ] )
		final Value<?> iterable = argument(0, arguments);

		// 1. If NewTarget is undefined, throw a TypeError exception.
		// 2. Let map be ? OrdinaryCreateFromConstructor(NewTarget, "%Map.prototype%", « [[MapData]] »).
		// 3. Set map.[[MapData]] to a new empty List.
		final ArrayList<MapEntry> mapData = new ArrayList<>();
		final MapObject map = new MapObject(interpreter.intrinsics, mapData);
		// 4. If iterable is either undefined or null, return map.
		if (iterable.isNullish()) return map;
		// 5. Let adder be ? Get(map, "set").
		final Value<?> adder_ = map.get(interpreter, Names.set);
		// 6. If IsCallable(adder) is false, throw a TypeError exception.
		final Executable adder = Executable.getExecutable(interpreter, adder_);
		// 7. Return ? AddEntriesFromIterable(map, iterable, adder).
		return addEntriesFromIterable(interpreter, map, iterable, adder);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-add-entries-from-iterable")
	@NonCompliant
	private MapObject addEntriesFromIterable(Interpreter interpreter, MapObject target, Value<?> iterable, Executable adder) throws AbruptCompletion {
		// 24.1.1.2 AddEntriesFromIterable ( target, iterable, adder )

		// 1. Let iteratorRecord be ? GetIterator(iterable, sync).
		final var iteratorRecord = getIterator(interpreter, iterable);
		// 2. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iteratorRecord).
			final ObjectValue next = iteratorRecord.step(interpreter);
			// b. If next is false, return target.
			if (next == null) return target;
			// c. Let nextItem be ? IteratorValue(next).
			final Value<?> nextItem = iteratorValue(interpreter, next);
			// d. If nextItem is not an Object, then
			if (!(nextItem instanceof final ObjectValue nextObject)) {
				// i. Let error be ThrowCompletion(a newly created TypeError object).
				// FIXME: ii. Return ? IteratorClose(iteratorRecord, error).
				final String message = "Iterator value %s is not an entry object".formatted(nextItem.toDisplayString(true));
				throw error(new TypeError(interpreter, message));
			}

			// e. Let k be Completion(Get(nextItem, "0")).
			final Value<?> k = nextObject.get(interpreter, new StringValue("0"));
			// FIXME: f. IfAbruptCloseIterator(k, iteratorRecord).
			// g. Let v be Completion(Get(nextItem, "1")).
			final Value<?> v = nextObject.get(interpreter, new StringValue("1"));
			// FIXME: h. IfAbruptCloseIterator(v, iteratorRecord).
			// i. Let status be Completion(Call(adder, target, « k, v »)).
			adder.call(interpreter, target, k, v);
			// FIXME: j. IfAbruptCloseIterator(status, iteratorRecord).
		}
	}

	@Override
	public MapObject internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Map constructor must be called with `new`"));
	}
}
