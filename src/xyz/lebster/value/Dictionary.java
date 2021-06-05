package xyz.lebster.value;

import xyz.lebster.node.Identifier;

import java.util.HashMap;

public class Dictionary extends Value<HashMap<Identifier, Value<?>>> {
	public Dictionary() {
		super(Type.Dictionary, new HashMap<>());
	}

	public Dictionary(HashMap<Identifier, Value<?>> value) {
		super(Type.Dictionary, value);
	}

	public Value<?> set(Identifier name, Value<?> value) {
		return this.value.put(name, value);
	}

	public Value<?> get(Identifier name) {
		return this.value.get(name);
	}

	public boolean containsKey(Identifier name) {
		return this.value.containsKey(name);
	}
}
