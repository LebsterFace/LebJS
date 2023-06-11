package xyz.lebster.core.value.map;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.ArrayList;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-objects")
public final class MapObject extends ObjectValue {
	public final ArrayList<MapEntry> mapData;

	static final class MapEntry {
		public Value<?> key;
		public Value<?> value;

		public MapEntry(Value<?> key, Value<?> value) {
			this.key = key;
			this.value = value;
		}
	}


	public MapObject(Intrinsics intrinsics, ArrayList<MapEntry> mapData) {
		super(intrinsics.mapPrototype);
		this.mapData = mapData;
	}
}
