package xyz.lebster.node.value;

import xyz.lebster.ANSI;
import xyz.lebster.Dumper;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.runtime.prototype.ObjectPrototype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dictionary extends Value<Map<StringLiteral, Value<?>>> {
	public Dictionary(Map<StringLiteral, Value<?>> value) {
		super(value, Type.Dictionary);
	}

	public Dictionary() {
		this(new HashMap<>());
	}

	@Override
	public Primitive<?> toPrimitive(Type preferredType) {
		throw new NotImplemented("Dictionary#toPrimitive");
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return toPrimitive(Type.Number).toNumericLiteral();
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(true);
	}

	@Override
	public Dictionary toDictionary() {
		return this;
	}

	public Dictionary set(StringLiteral key, Value<?> value) {
		this.value.put(key, value);
		return this;
	}

	public Dictionary set(String key, Value<?> value) {
		return set(new StringLiteral(key), value);
	}

	public Value<?> get(StringLiteral key) {
		Dictionary object = this;

		while (object != null) {
			if (object.value.containsKey(key)) {
				// Property was found
				return object.value.get(key);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}

		// End of prototype chain; property does not exist.
		return new Undefined();
//		throw new ExecutionError("Property '" + key.value + "' does not exist on object!");
	}

	public boolean hasProperty(StringLiteral name) {
		if (hasOwnProperty(name)) return true;
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
		final Primitive<?> primValue = toPrimitive(Type.String);
		return primValue.toString();
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
			if (value instanceof final Dictionary dictionary) {
				if (parents.contains(dictionary)) {
					System.out.print(ANSI.RED);
					System.out.print(this == value ? "[self]" : "[parent]");
					System.out.println(ANSI.RESET);
				} else {
					System.out.println();
					dictionary.dumpRecursive(indent + 2, parents);
				}
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

	@Override
	public String typeOf() {
		return "object";
	}

	@Override
	public void represent(StringRepresentation representation) {
		if (value.isEmpty()) {
			representation.append("{}");
			return;
		}

		System.err.println(ANSI.BACKGROUND_BRIGHT_YELLOW + "WARNING" + ANSI.RESET + " Dictionary#represent for filled dictionary has not been implemented!");
	}
}