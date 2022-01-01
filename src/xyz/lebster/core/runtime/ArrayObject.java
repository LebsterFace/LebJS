package xyz.lebster.core.runtime;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
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
		public void set(Interpreter interpreter, Value<?> value) {
			throw new NotImplemented("Setter for <Array>.length");
		}
	});

	public ArrayObject(Value<?>[] values) {
		this.length = values.length;
		this.set(LENGTH_KEY, LENGTH_GETTER_SETTER);

		for (int i = 0; i < values.length; i++) {
			this.set(String.valueOf(i), values[i]);
		}
	}

	@Override
	public Dictionary getPrototype() {
		return ArrayPrototype.instance;
	}
}