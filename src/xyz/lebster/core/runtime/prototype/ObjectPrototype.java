package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.node.value.Dictionary;

public final class ObjectPrototype extends Dictionary {
	public static final ObjectPrototype instance = new ObjectPrototype();
	private ObjectPrototype() {}

	@Override
	public Dictionary getPrototype() {
		return null;
	}
}