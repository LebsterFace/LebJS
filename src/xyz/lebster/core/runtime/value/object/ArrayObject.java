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
			this.arrayValues.add(new DataDescriptor(value, true, true, true));
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
						arrayValues.add(null);
				} else {
					arrayValues.subList(newLen, arrayValues.size()).clear();
				}
			}
		});
	}

	public ArrayObject(ArrayList<Value<?>> arrayValues) {
		this(arrayValues.toArray(new Value[0]));
	}

	@Override
	public PropertyDescriptor getOwnProperty(Key<?> key) {
		final PropertyDescriptor fromMap = this.value.get(key);
		if (fromMap != null) return fromMap;
		final int index = key.toIndex();
		return index != -1 && index < arrayValues.size() ? arrayValues.get(index) : null;
	}

	@Override
	public ArrayList<Map.Entry<Key<?>, PropertyDescriptor>> entries() {
		final ArrayList<Map.Entry<Key<?>, PropertyDescriptor>> result = new ArrayList<>();

		for (int i = 0; i < arrayValues.size(); i++) {
			PropertyDescriptor arrayValue = arrayValues.get(i);
			result.add(Map.entry(new StringValue(i), arrayValue));
		}

		result.addAll(value.entrySet());
		return result;
	}

	@Override
	public boolean hasOwnProperty(Key<?> key) {
		if (super.hasOwnProperty(key)) return true;
		final int index = key.toIndex();
		return index != -1 && index < arrayValues.size() && arrayValues.get(index) != null;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-exotic-objects-defineownproperty-p-desc")
	@Override
	@NonCompliant
	public boolean defineOwnProperty(Interpreter interpreter, Key<?> P, PropertyDescriptor Desc) throws AbruptCompletion {
		final int arrayIndex = P.toIndex();
		if (arrayIndex != -1) {
			final long indexLong = P.toNumberValue(interpreter).toUint32();
			if (indexLong > Integer.MAX_VALUE) throw new NotImplemented("Arrays longer than 2^31-1");
			final int index = (int) indexLong;
			final int delta = (index + 1) - arrayValues.size();
			for (int i = 0; i < delta; i++)
				arrayValues.add(new DataDescriptor(Undefined.instance, true, true, true));
			arrayValues.set(index, Desc);
			return true;
		}

		return super.defineOwnProperty(interpreter, P, Desc);
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
			PropertyDescriptor next = iterator.next();
			if (next != null) {
				next.display(representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
			}

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

	public Value<?>[] values(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] result = new Value[arrayValues.size()];
		for (int i = 0; i < arrayValues.size(); i++) {
			final PropertyDescriptor desc = arrayValues.get(i);
			if (desc != null) result[i] = desc.get(interpreter, this);
		}

		return result;
	}
}