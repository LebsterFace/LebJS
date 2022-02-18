package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.property.NativeAccessorDescriptor;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.ArrayPrototype;

public final class ArrayObject extends ObjectValue implements HasBuiltinTag {
	private int length;

	public final NativeAccessorDescriptor LENGTH_GETTER_SETTER = new NativeAccessorDescriptor() {
		@Override
		public Value<?> get(Interpreter interpreter, ObjectValue thisValue) {
			return new NumberValue(length);
		}

		@Override
		@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
		@NonCompliant
		public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> value) throws AbruptCompletion {
			final int newLen = (int) Math.floor(value.toNumberValue(interpreter).value);
			if (newLen > length) {
				ArrayObject.this.length = newLen;
			} else {
				throw new NotImplemented("Reducing array length");
			}
		}
	};

	public ArrayObject(Value<?>... values) {
		this.length = values.length;
		this.value.put(Names.length, LENGTH_GETTER_SETTER);

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