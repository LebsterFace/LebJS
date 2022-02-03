package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.native_.NativeGetterSetter;
import xyz.lebster.core.runtime.value.native_.NativeProperty;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

import java.util.HashSet;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag {
	private int length;

	public final NativeProperty LENGTH_GETTER_SETTER = new NativeProperty(new NativeGetterSetter() {
		@Override
		public Value<?> get(Interpreter interpreter) {
			return new NumberValue(length);
		}

		@Override
		@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
		// FIXME: Follow spec
		public void set(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
			final int newLen = (int) Math.floor(value.toNumberValue(interpreter).value);
			if (newLen > length) {
				ArrayObject.this.length = newLen;
			} else {
				throw new NotImplemented("Reducing array length");
			}
		}
	});

	public ArrayObject(Value<?>... values) {
		this.length = values.length;
		this.put(Names.length, LENGTH_GETTER_SETTER);

		for (int i = 0; i < values.length; i++) {
			if (values[i] != null)
				this.put(String.valueOf(i), values[i]);
		}
	}

	public void forEach(Interpreter interpreter, ForEachCallback callback) throws AbruptCompletion {
		for (int index = 0; index < this.length; index++) {
			final StringValue key = new StringValue(index);
			if (this.hasProperty(key))
				callback.run(this.get(interpreter, key), index);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append('[');
		if (value.isEmpty()) {
			representation.append(']');
			return;
		}

		parents.add(this);
		for (int index = 0; index < this.length; index++) {
			final Value<?> element = this.value.get(new StringValue(index)).getRawValue();
			if (element instanceof final ObjectValue object) {
				if (parents.contains(object)) {
					representation.append(ANSI.RED);
					representation.append(this == element ? "[self]" : "[parent]");
					representation.append(ANSI.RESET);
				} else {
					object.displayRecursive(representation, (HashSet<ObjectValue>) parents.clone(), singleLine);
				}
			} else {
				element.display(representation);
			}

			if (index != this.length - 1)
				representation.append(", ");
		}

		representation.append(']');
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return ArrayPrototype.instance;
	}

	@Override
	public String getBuiltinTag() {
		return "Array";
	}

	@FunctionalInterface
	public interface ForEachCallback {
		void run(Value<?> value, int index) throws AbruptCompletion;
	}
}