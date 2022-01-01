package xyz.lebster.core.runtime;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.prototype.ArrayPrototype;


public final class ArrayObject extends Dictionary {
	public final static StringLiteral LENGTH_KEY = new StringLiteral("length");
	private int length;

	public final NativeProperty LENGTH_GETTER_SETTER = new NativeProperty(new NativeGetterSetter() {
		@Override
		public Value<?> get(Interpreter interpreter) {
			return new NumericLiteral(length);
		}

		@Override
		@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-arraysetlength")
		// FIXME: Follow spec
		public void set(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
			final int newLen = (int) Math.floor(value.toNumericLiteral(interpreter).value);
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
			this.put(String.valueOf(i), values[i]);
		}
	}

	@Override
	public Dictionary getPrototype() {
		return ArrayPrototype.instance;
	}
}