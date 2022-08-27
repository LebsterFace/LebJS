package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.PropertyDescriptor;

import java.util.Iterator;
import java.util.Map;
import java.util.PrimitiveIterator;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class StringWrapper extends PrimitiveWrapper<StringValue, StringPrototype> implements HasBuiltinTag {
	public StringWrapper(StringPrototype prototype, StringValue data) {
		super(prototype, data);
		this.put(Names.length, new NumberValue(data.value.length()));
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
	public Iterable<Map.Entry<Key<?>, PropertyDescriptor>> entries() {
		return () -> new Iterator<>() {
			private final PrimitiveIterator.OfInt primitiveIterator = data.value.chars().iterator();
			private final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> x = StringWrapper.super.entries().iterator();
			private int charIndex = 0;

			@Override
			public boolean hasNext() {
				return primitiveIterator.hasNext() || x.hasNext();
			}

			@Override
			public Map.Entry<Key<?>, PropertyDescriptor> next() {
				if (primitiveIterator.hasNext()) {
					final var key = new StringValue(charIndex);
					final var value = new DataDescriptor(new StringValue((char) (int) primitiveIterator.next()), false, true, false);
					charIndex++;

					return new Map.Entry<>() {
						@Override
						public Key<?> getKey() {
							return key;
						}

						@Override
						public PropertyDescriptor getValue() {
							return value;
						}

						@Override
						public PropertyDescriptor setValue(PropertyDescriptor value) {
							throw new ShouldNotHappen("Strings are immutable");
						}
					};
				}

				return x.next();
			}
		};
	}

	@Override
	public String getBuiltinTag() {
		return "String";
	}
}