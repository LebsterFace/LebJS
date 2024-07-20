package xyz.lebster.core.value.set;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Displayable;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.ArrayList;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set-objects")
public final class SetObject extends ObjectValue {
	public final ArrayList<Value<?>> setData;

	public SetObject(Intrinsics intrinsics, ArrayList<Value<?>> setData) {
		super(intrinsics.setPrototype);
		this.setData = setData;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-setdatasize")
	public int size() {
		// 1. Let count be 0.
		int count = 0;
		// 2. For each element e of setData, do
		for (final Value<?> e : setData) {
			// a. If e is not EMPTY, set count to count + 1.
			if (e != null) count = count + 1;
		}

		// 3. Return count.
		return count;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-set.prototype.size")
	NumberValue getSize() {
		// 3. Let size be SetDataSize(S.[[SetData]]).
		// 4. Return 𝔽(size).
		return new NumberValue(size());
	}

	@Override
	public Iterable<Displayable> displayableValues() {
		final ArrayList<Displayable> result = new ArrayList<>(setData.size());
		for (final Value<?> e : setData) {
			if (e != null) {
				result.add(e);
			}
		}

		return result;
	}

	@Override
	public void displayPrefix(StringBuilder builder) {
		builder.append("Set");
		builder.append('(');
		getSize().display(builder);
		builder.append(')');
	}
}
