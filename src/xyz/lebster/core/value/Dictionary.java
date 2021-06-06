package xyz.lebster.core.value;

import xyz.lebster.core.node.Identifier;
import xyz.lebster.core.runtime.Interpreter;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("Dictionary:");
		for (Map.Entry<Identifier, Value<?>> entry : this.value.entrySet()) {
			System.out.print(entry.getKey());
			System.out.print(": ");
			entry.getValue().dump(0);
		}
	}
}
