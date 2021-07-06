package xyz.lebster.node.value.prototype;

import xyz.lebster.node.value.Dictionary;

public class ObjectPrototype extends Dictionary {
	public static final ObjectPrototype instance = new ObjectPrototype();
	private ObjectPrototype() {}

	@Override
	public Dictionary getPrototype() {
		return null;
	}
}
