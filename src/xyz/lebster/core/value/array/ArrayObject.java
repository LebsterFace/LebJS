package xyz.lebster.core.value.array;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Displayable;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

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

		putAccessor(interpreter.intrinsics, Names.length, this::getLength, this::arraySetLength, false, false);
	}

	public <T extends Value<?>> ArrayObject(Interpreter interpreter, List<T> arrayValues) {
		this(interpreter, arrayValues.toArray(new Value[0]));
	}

	public ArrayObject(Interpreter interpreter, int length) {
		this(interpreter, new Value<?>[length]);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
	public Undefined arraySetLength(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> value = argument(0, arguments);

		final long newLen = value.toNumberValue(interpreter).toUint32();
		final NumberValue numberLen = value.toNumberValue(interpreter);
		if (newLen != numberLen.value)
			throw error(new RangeError(interpreter, "Invalid array length"));

		if (newLen > arrayValues.size()) {
			final long delta = newLen - arrayValues.size();
			for (int i = 0; i < delta; i++)
				arrayValues.add(null);
		} else if (newLen < arrayValues.size()) {
			arrayValues.subList(Math.toIntExact(newLen), arrayValues.size()).clear();
		}

		return Undefined.instance;
	}

	private NumberValue getLength(Interpreter interpreter1, Value<?>[] arguments) {
		return new NumberValue(arrayValues.size());
	}

	@Override
	protected void internalDeleteProperty(Key<?> P) {
		super.internalDeleteProperty(P);
		final int index = P.toIndex();
		if (index != -1 && index < arrayValues.size())
			arrayValues.set(index, null);
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
				arrayValues.add(null);
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
	public Iterable<Displayable> displayableValues() {
		return Collections.unmodifiableList(arrayValues);
	}

	@Override
	public Iterable<Entry<Key<?>, PropertyDescriptor>> displayableProperties() {
		final ArrayList<Entry<Key<?>, PropertyDescriptor>> nonLengthProperties = new ArrayList<>();
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