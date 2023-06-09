package xyz.lebster.core.value.map;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.clear")
	private static Value<?> clear(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.1 Map.prototype.clear ( )

		throw new NotImplemented("Map.prototype.clear");
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.delete")
	private static Value<?> deleteMethod(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.3 Map.prototype.delete ( key )
		final Value<?> key = argument(0, arguments);

		throw new NotImplemented("Map.prototype.delete");
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.entries")
	private static Value<?> entries(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.4 Map.prototype.entries ( )

		throw new NotImplemented("Map.prototype.entries");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.forEach")
	private static Value<?> forEach(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.5 Map.prototype.forEach ( callbackfn [ , thisArg ] )
		final Value<?> callbackfn = argument(0, arguments);
		final Value<?> thisArg = argument(1, arguments);

		throw new NotImplemented("Map.prototype.forEach");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.get")
	private static Value<?> getMethod(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.6 Map.prototype.get ( key )
		final Value<?> key = argument(0, arguments);

		throw new NotImplemented("Map.prototype.get");
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.has")
	private static Value<?> has(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.7 Map.prototype.has ( key )
		final Value<?> key = argument(0, arguments);

		throw new NotImplemented("Map.prototype.has");
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.keys")
	private static Value<?> keys(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.8 Map.prototype.keys ( )

		throw new NotImplemented("Map.prototype.keys");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.set")
	private static Value<?> setMethod(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.9 Map.prototype.set ( key, value )
		final Value<?> key = argument(0, arguments);
		final Value<?> value = argument(1, arguments);

		throw new NotImplemented("Map.prototype.set");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-map.prototype.size")
	private static NumberValue getSize(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.10 get Map.prototype.size

		throw new NotImplemented("get Map.prototype.size");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map.prototype.values")
	private static Value<?> values(Interpreter interpreter, Value<?>[] arguments) {
		// 24.1.3.11 Map.prototype.values ( )

		throw new NotImplemented("Map.prototype.values");
	}
}