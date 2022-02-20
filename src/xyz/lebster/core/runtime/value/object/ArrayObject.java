package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.property.DataDescriptor;
import xyz.lebster.core.runtime.value.object.property.NativeAccessorDescriptor;
import xyz.lebster.core.runtime.value.object.property.PropertyDescriptor;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag {
	private final ArrayList<Value<?>> arrayValues;

	public ArrayObject(Value<?>... initialValues) {
		this(new ArrayList<>(Arrays.asList(initialValues)));
	}

	public ArrayObject(ArrayList<Value<?>> arrayValues) {
		this.arrayValues = arrayValues;
		this.value.put(Names.length, new NativeAccessorDescriptor(false) {
			@Override
			public Value<?> get(Interpreter interpreter, ObjectValue thisValue) {
				return new NumberValue(arrayValues.size());
			}

			@Override
			@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
			public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> value) throws AbruptCompletion {
				final int newLen = (int) Math.floor(value.toNumberValue(interpreter).value);
				if (newLen == arrayValues.size()) return;

				if (newLen > arrayValues.size()) {
					final int delta = newLen - arrayValues.size();
					arrayValues.addAll(Collections.nCopies(delta, null));
				} else {
					arrayValues.subList(newLen, arrayValues.size()).clear();
				}
			}
		});
	}

	private Value<?> getArrayValueOrNull(Key<?> key) {
		if (!(key instanceof StringValue stringValue)) return null;
		if (stringValue.value.length() == 0) return null;

		int index = 0;
		for (int i = 0; i < stringValue.value.length(); i++) {
			final char c = stringValue.value.charAt(i);
			if (c < '0' || c > '9') return null;

			index *= 10;
			index += c - '0';
			if (index >= arrayValues.size()) return null;
		}

		return arrayValues.get(index);
	}

	@Override
	public PropertyDescriptor getOwnProperty(Key<?> key) {
		final PropertyDescriptor fromMap = this.value.get(key);
		if (fromMap != null) return fromMap;
		final Value<?> arrayValue = getArrayValueOrNull(key);
		if (arrayValue == null) return null;
		return new DataDescriptor(arrayValue, true, true, true);
	}

	@Override
	public boolean hasOwnProperty(Key<?> key) {
		return this.value.containsKey(key) || getArrayValueOrNull(key) != null;
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return ArrayPrototype.instance;
	}

	@Override
	public String getBuiltinTag() {
		return "Array";
	}
}