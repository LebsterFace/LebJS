package xyz.lebster.core.value.map;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-objects")
public final class MapObject extends ObjectValue {
	public MapObject(Intrinsics intrinsics) {
		super(intrinsics.mapPrototype);
	}
}
