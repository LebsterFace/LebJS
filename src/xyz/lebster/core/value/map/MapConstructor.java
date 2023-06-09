package xyz.lebster.core.value.map;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-constructor")
public final class MapConstructor extends BuiltinConstructor<MapObject, MapPrototype> {
	public MapConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Map, 0);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-iterable")
	public MapObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		return new MapObject(interpreter.intrinsics);
	}

	@Override
	public MapObject internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Map constructor must be called with `new`"));
	}
}
