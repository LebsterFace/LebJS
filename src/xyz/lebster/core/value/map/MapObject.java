package xyz.lebster.core.value.map;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Displayable;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.ArrayList;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-map-objects")
public final class MapObject extends ObjectValue {
	public final ArrayList<MapEntry> mapData;

	public MapObject(Intrinsics intrinsics, ArrayList<MapEntry> mapData) {
		super(intrinsics.mapPrototype);
		this.mapData = mapData;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-map.prototype.size")
	NumberValue getSize() {
		// 3. Let count be 0.
		int count = 0;
		// 4. For each Record { [[Key]], [[Value]] } p of M.[[MapData]], do
		for (final MapEntry p : mapData) {
			// a. If p.[[Key]] is not empty, set count to count + 1.
			if (p != null) count += 1;
		}

		// 5. Return 𝔽(count).
		return new NumberValue(count);
	}

	@Override
	public Iterable<Displayable> displayableValues() {
		final ArrayList<Displayable> result = new ArrayList<>(mapData.size());
		for (final MapEntry p : mapData) {
			if (p != null) {
				result.add(p);
			}
		}

		return result;
	}

	@Override
	public void displayPrefix(StringBuilder builder) {
		builder.append("Map");
		builder.append('(');
		getSize().display(builder);
		builder.append(')');
	}

	public static final class MapEntry implements Displayable {
		public Value<?> key;
		public Value<?> value;

		public MapEntry(Value<?> key, Value<?> value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public void display(StringBuilder builder) {
			key.display(builder);
			builder.append(" => ");
			value.display(builder);
		}
	}
}
