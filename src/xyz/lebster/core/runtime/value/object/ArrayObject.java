package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
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
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag, Iterable<PropertyDescriptor> {
	private final ArrayList<PropertyDescriptor> arrayValues;

	public ArrayObject(Value<?>... initialValues) {
		this.arrayValues = new ArrayList<>(initialValues.length);
		for (final Value<?> value : initialValues) {
			this.arrayValues.add(new DataDescriptor(value));
		}

		this.value.put(Names.length, new NativeAccessorDescriptor(false, false) {
			@Override
			public Value<?> get(Interpreter interpreter, ObjectValue thisValue) {
				return new NumberValue(arrayValues.size());
			}

			@Override
			@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
			public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> value1) throws AbruptCompletion {
				final int newLen = (int) Math.floor(value1.toNumberValue(interpreter).value);
				if (newLen == arrayValues.size()) return;

				if (newLen > arrayValues.size()) {
					final int delta = newLen - arrayValues.size();
					for (int i = 0; i < delta; i++)
						arrayValues.add(new DataDescriptor(Undefined.instance));
				} else {
					arrayValues.subList(newLen, arrayValues.size()).clear();
				}
			}
		});
	}

	public ArrayObject(ArrayList<Value<?>> arrayValues) {
		this(arrayValues.toArray(new Value[0]));
	}

	private static int getArrayIndex(Key<?> key) {
		if (!(key instanceof StringValue stringValue)) return -1;
		if (stringValue.value.length() == 0) return -1;

		int index = 0;
		for (int i = 0; i < stringValue.value.length(); i++) {
			final char c = stringValue.value.charAt(i);
			if (c < '0' || c > '9') return -1;

			index *= 10;
			index += c - '0';
		}

		return index;
	}

	private PropertyDescriptor getArrayPropertyOrNull(Key<?> key) {
		final int index = getArrayIndex(key);
		if (index == -1 || index >= arrayValues.size()) return null;
		return arrayValues.get(index);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-exotic-objects-defineownproperty-p-desc")
	@Override
	@NonCompliant
	public boolean defineOwnProperty(Interpreter interpreter, Key<?> P, PropertyDescriptor Desc) throws AbruptCompletion {
		final int arrayIndex = getArrayIndex(P);
		if (arrayIndex != -1) {
			final long indexLong = P.toNumberValue(interpreter).toUint32();
			if (indexLong > Integer.MAX_VALUE) throw new NotImplemented("Arrays longer than 2^31-1");
			final int index = (int) indexLong;
			final int delta = (index + 1) - arrayValues.size();
			for (int i = 0; i < delta; i++)
				arrayValues.add(new DataDescriptor(Undefined.instance));
			arrayValues.set(index, Desc);
			return true;
		}

		return super.defineOwnProperty(interpreter, P, Desc);
	}

	@Override
	public PropertyDescriptor getOwnProperty(Key<?> key) {
		final PropertyDescriptor fromMap = this.value.get(key);
		if (fromMap != null) return fromMap;
		return getArrayPropertyOrNull(key);
	}

	@Override
	public boolean hasOwnProperty(Key<?> key) {
		return this.value.containsKey(key) || getArrayPropertyOrNull(key) != null;
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
	public Iterator<PropertyDescriptor> iterator() {
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

	@SuppressWarnings("unchecked")
	private void representValues(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		final Iterator<PropertyDescriptor> iterator = arrayValues.iterator();
		final ArrayList<Map.Entry<Key<?>, PropertyDescriptor>> nonLengthMapIterator = new ArrayList<>();
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : value.entrySet()) {
			if (entry.getKey() == Names.length) continue;
			nonLengthMapIterator.add(entry);
		}

		final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> mapIterator = nonLengthMapIterator.iterator();

		while (iterator.hasNext()) {
			if (!singleLine) representation.appendIndent();
			iterator.next().display(representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
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