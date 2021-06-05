package xyz.lebster.value;

import java.util.HashMap;

public class Dictionary extends Value<HashMap<String, Value<?>>> {
	public Dictionary() {
		super(Type.Dictionary, new HashMap<>());
	}

	public Dictionary(HashMap<String, Value<?>> value) {
		super(Type.Dictionary, value);
	}

	public Value<?> set(String name, Value<?> value) {
		return this.value.put(name, value);
	}

	public Value<?> get(String name) {
		return this.value.get(name);
	}
}
