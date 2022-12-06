package xyz.lebster.core.value.array;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.NativeAccessorDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.*;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag, Iterable<PropertyDescriptor> {
	private final ArrayList<PropertyDescriptor> arrayValues;

	public ArrayObject(Interpreter interpreter, Value<?>... initialValues) {
		super(interpreter.intrinsics.arrayPrototype);
		this.arrayValues = new ArrayList<>(initialValues.length);
		for (final Value<?> value : initialValues) {
			if (value == null) {
				// null indicates missing value
				this.arrayValues.add(null);
			} else {
				this.arrayValues.add(new DataDescriptor(value, true, true, true));
			}
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

	public <T extends Value<?>> ArrayObject(Interpreter interpreter, List<T> arrayValues) {
		this(interpreter, arrayValues.toArray(new Value[0]));
	}

	public ArrayObject(Interpreter interpreter, int length) {
		this(interpreter, new Value<?>[length]);
	}

	private static void representEmpty(StringRepresentation representation, int emptyCount) {
		representation.append(ANSI.BRIGHT_BLACK);
		representation.append(emptyCount == 1 ? "empty" : "empty x " + emptyCount);
		representation.append(ANSI.RESET);
	}

	@Override
	public PropertyDescriptor getOwnProperty(Key<?> key) {
		final PropertyDescriptor fromMap = this.value.get(key);
		if (fromMap != null) return fromMap;
		final int index = key.toIndex();
		return index != -1 && index < arrayValues.size() ? arrayValues.get(index) : null;
	}

	@Override
	// TODO: Follow specified order
	public Iterable<Key<?>> ownPropertyKeys() {
		final ArrayList<Key<?>> result = new ArrayList<>();
		for (int i = 0; i < arrayValues.size(); i++)
			result.add(new StringValue(i));
		result.addAll(value.keySet());
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
		final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> propertiesIterator = nonLengthProperties().iterator();
		final Iterator<PropertyDescriptor> elementsIterator = arrayValues.iterator();
		int emptyCount = 0;
		while (elementsIterator.hasNext()) {
			if (!singleLine) representation.appendIndent();
			final PropertyDescriptor next = elementsIterator.next();
			if (next == null) {
				emptyCount++;
				continue;
			}

			if (emptyCount > 0) {
				representEmpty(representation, emptyCount);
				representPropertyDelimiter(true, representation, singleLine);
				emptyCount = 0;
			}

			next.display(representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
			representPropertyDelimiter(elementsIterator.hasNext() || propertiesIterator.hasNext(), representation, singleLine);
		}

		if (emptyCount > 0) {
			representEmpty(representation, emptyCount);
			representPropertyDelimiter(propertiesIterator.hasNext(), representation, singleLine);
		}

		representProperties(representation, parents, singleLine, propertiesIterator);
	}

	private ArrayList<Map.Entry<Key<?>, PropertyDescriptor>> nonLengthProperties() {
		final ArrayList<Map.Entry<Key<?>, PropertyDescriptor>> nonLengthProperties = new ArrayList<>();
		for (final var entry : value.entrySet()) {
			if (entry.getKey().equalsKey(Names.length)) continue;
			nonLengthProperties.add(entry);
		}

		return nonLengthProperties;
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