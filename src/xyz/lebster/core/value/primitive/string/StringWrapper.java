package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.util.Iterator;
import java.util.PrimitiveIterator;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class StringWrapper extends PrimitiveWrapper<StringValue, StringPrototype> implements HasBuiltinTag {
	public StringWrapper(Intrinsics intrinsics, StringValue data) {
		super(intrinsics.stringPrototype, data);
		this.put(Names.length, new NumberValue(data.value.length()), false, false, false);
	}

	@Override
	public PropertyDescriptor getOwnProperty(Key<?> key) {
		final PropertyDescriptor fromMap = this.value.get(key);
		if (fromMap != null) return fromMap;

		final int index = key.toIndex();
		if (index == -1 || index >= this.data.value.length()) return null;
		final var character = new StringValue(this.data.value.charAt(index));
		return new DataDescriptor(character, false, true, false);
	}

	@Override
	public boolean hasOwnProperty(Key<?> key) {
		if (this.value.containsKey(key)) return true;
		final int index = key.toIndex();
		return index != -1 && index < this.data.value.length();
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-exotic-objects-ownpropertykeys")
	@NonCompliant
	public Iterable<Key<?>> ownPropertyKeys() {
		return () -> new Iterator<>() {
			private final PrimitiveIterator.OfInt codePoints = data.value.codePoints().iterator();
			private final Iterator<Key<?>> properties = StringWrapper.super.ownPropertyKeys().iterator();
			private int codePointIndex = 0;

			@Override
			public boolean hasNext() {
				return codePoints.hasNext() || properties.hasNext();
			}

			@Override
			public Key<?> next() {
				if (codePoints.hasNext()) {
					codePoints.next();
					final var key = new StringValue(codePointIndex);
					codePointIndex++;
					return key;
				} else {
					return properties.next();
				}
			}
		};
	}

	@Override
	public String getBuiltinTag() {
		return "String";
	}
}