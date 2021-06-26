package xyz.lebster.core.value.prototype;

import xyz.lebster.core.value.Dictionary;

public class ObjectPrototype extends Dictionary {
	public static final ObjectPrototype instance = new ObjectPrototype();

	public ObjectPrototype() {}

	@Override
	public Dictionary getPrototype() {
		return null;
	}
}
