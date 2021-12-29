package xyz.lebster.core.runtime;

import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.prototype.ArrayPrototype;


public final class ArrayObject extends Dictionary {
	public final static StringLiteral LENGTH_KEY = new StringLiteral("length");
	private int length;

	public ArrayObject(Value<?>[] values) {
		this.set(LENGTH_KEY, new NumericLiteral(values.length));
		this.length = values.length;

		for (int i = 0; i < values.length; i++) {
			this.set(String.valueOf(i), values[i]);
		}
	}

	@Override
	public Dictionary getPrototype() {
		return ArrayPrototype.instance;
	}
}