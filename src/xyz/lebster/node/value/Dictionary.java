package xyz.lebster.node.value;

import xyz.lebster.ANSI;
import xyz.lebster.Dumper;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.prototype.ObjectPrototype;

import java.util.*;

public class Dictionary extends Value<Map<StringLiteral, Value<?>>> {
	public Dictionary(Map<StringLiteral, Value<?>> value) {
		super(value, Type.Dictionary);
	}

	public Dictionary() {
		super(new HashMap<>(), Type.Dictionary);
	}

	@Override
	public Primitive<?> toPrimitive(Interpreter interpreter, Type preferredType) {
		throw new NotImplemented("Dictionary#toPrimitive");
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return toPrimitive(interpreter, Type.Number).toNumericLiteral(interpreter);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(true);
	}

	@Override
	public Dictionary toDictionary(Interpreter interpreter) {
		return this;
	}

	public Dictionary set(StringLiteral key, Value<?> value) {
		this.value.put(key, value);
		return this;
	}

	public Dictionary set(String key, Value<?> value) {
		return set(new StringLiteral(key), value);
	}

	public Value<?> get(StringLiteral name) {
		Dictionary object = this;

		while (object != null) {
			if (object.value.containsKey(name)) {
				// Property was found
				return object.value.get(name);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}

		// End of prototype chain; property does not exist.
		return new Undefined();
	}

	public boolean hasProperty(StringLiteral name) {
		Dictionary object = this;

		while (object != null) {
			if (object.value.containsKey(name)) {
				// Property was found
				return true;
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}

		// End of prototype chain; property does not exist.
		return false;
	}

	public boolean hasOwnProperty(StringLiteral key) {
		return this.value.containsKey(key);
	}

	@Override
	public String toString() {
		return "[object Object]";
	}

	@Override
	public void dump(int indent) {
		dumpRecursive(indent, new LinkedList<>());
	}

	protected void dumpRecursive(int indent, List<Dictionary> parents) {
		Dumper.dumpName(indent, "Dictionary");
		if (value.isEmpty()) {
			Dumper.dumpIndent(indent + 1);
			System.out.print(ANSI.RED);
			System.out.print("[empty]");
			System.out.println(ANSI.RESET);
			return;
		}

		parents.add(this);
		for (HashMap.Entry<StringLiteral, Value<?>> entry : value.entrySet()) {
			Dumper.dumpIndent(indent + 1);
			System.out.print(entry.getKey().value);
			System.out.print(": ");
			final Value<?> value = entry.getValue();
			if (parents.contains(value)) {
				System.out.print(ANSI.RED);
				System.out.print(this == value ? "[self]" : "[parent]");
				System.out.println(ANSI.RESET);
			} else if (value instanceof final Dictionary dictionary) {
				System.out.println();
				dictionary.dumpRecursive(indent + 2, parents);
			} else {
				value.dump(0);
			}
		}
	}

	public Dictionary getPrototype() {
		return ObjectPrototype.instance;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
