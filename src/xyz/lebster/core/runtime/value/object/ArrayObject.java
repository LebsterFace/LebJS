package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.property.DataDescriptor;
import xyz.lebster.core.runtime.value.object.property.NativeAccessorDescriptor;
import xyz.lebster.core.runtime.value.object.property.PropertyDescriptor;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

import java.util.*;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag, Iterable<Value<?>> {
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

	@Override
	public Iterator<Value<?>> iterator() {
		return arrayValues.iterator();
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append("[ ");

		parents.add(this);
		if (!singleLine) {
			representation.appendLine();
			representation.indent();
		}

		this.representValues(representation, parents, singleLine);

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append(']');
	}

	private void representValues(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		final Iterator<Value<?>> iterator = ArrayObject.this.arrayValues.iterator();
		final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> mapIterator = ArrayObject.this.value.entrySet().iterator();

		while (iterator.hasNext()) {
			if (!singleLine) representation.appendIndent();
			DataDescriptor.display(iterator.next(), representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
			if (iterator.hasNext() || mapIterator.hasNext()) representation.append(',');
			if (singleLine) representation.append(' ');
			else representation.appendLine();
		}


		while (mapIterator.hasNext()) {
			final Map.Entry<Key<?>, PropertyDescriptor> entry = mapIterator.next();
			if (!singleLine) representation.appendIndent();
			representation.append(ANSI.BRIGHT_BLACK);
			entry.getKey().displayForObjectKey(representation);
			representation.append(ANSI.RESET);
			representation.append(": ");

			entry.getValue().display(representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
			if (iterator.hasNext()) representation.append(',');
			if (singleLine) representation.append(' ');
			else representation.appendLine();
		}
	}
}