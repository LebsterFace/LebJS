package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.native_.NativeGetterSetter;
import xyz.lebster.core.runtime.value.native_.NativeProperty;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

import java.util.HashSet;


public final class ArrayObject extends ObjectValue {
	public final static StringValue LENGTH_KEY = new StringValue("length");
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

	public ArrayObject(Value<?>[] values) {
		this.length = values.length;
		this.put(LENGTH_KEY, LENGTH_GETTER_SETTER);

		for (int i = 0; i < values.length; i++) {
			if (values[i] != null)
				this.put(String.valueOf(i), values[i]);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void displayRecursive(StringRepresentation builder, HashSet<ObjectValue> parents, boolean singleLine) {
		builder.append('[');
		if (value.isEmpty()) {
			builder.append(']');
			return;
		}

		parents.add(this);
		for (int index = 0; index < this.length; index++) {
			final Value<?> element = this.get(new StringValue(index));
			if (element instanceof final ObjectValue object) {
				if (parents.contains(object)) {
					builder.append(ANSI.RED);
					builder.append(this == element ? "[self]" : "[parent]");
					builder.append(ANSI.RESET);
				} else {
					object.displayRecursive(builder, (HashSet<ObjectValue>) parents.clone(), singleLine);
				}
			} else {
				element.display(builder);
			}

			if (index != this.length - 1)
				builder.append(", ");
		}

		builder.append(']');
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return ArrayPrototype.instance;
	}
}