package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.TypeError;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Dictionary extends Value<Map<Dictionary.Key<?>, Value<?>>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = Dictionary.LAST_UNUSED_IDENTIFIER++;

	public static abstract class Key<R> extends Primitive<R> {
		public Key(R value, Type type) {
			super(value, type);
		}
	};

	public Dictionary(Map<Key<?>, Value<?>> value) {
		super(value, Type.Dictionary);
	}

	public Dictionary() {
		this(new HashMap<>());
	}

	private static String getHint(Type preferredType) {
		// i. If preferredType is not present, let hint be "default".
		if (preferredType == null) return "default";
		// ii. Else if preferredType is string, let hint be "string".
		if (preferredType == Type.String) return "string";
			// iii. Else,
		else {
			// 1. Assert: preferredType is number.
			assert preferredType == Type.Number;
			// 2. Let hint be "number".
			return "number";
		}
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public Primitive<?> toPrimitive(Interpreter interpreter, Type preferredType) throws AbruptCompletion {
//		a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
		final Value<?> exoticToPrim = get(new StringLiteral("toPrimitive")); // FIXME: Symbol.toPrimitive

//		b. If exoticToPrim is not undefined, then
		if (exoticToPrim instanceof final Executable<?> executable) {
//			i - iii. Get hint
			final StringLiteral hint = new StringLiteral(getHint(preferredType));
//			iv. Let result be ? Call(exoticToPrim, input, hint).
			final Value<?> result = executable.call(interpreter, this, hint);
//			v. If Type(result) is not Object, return result.
			if (result.type != Type.Dictionary) return result.toPrimitive(interpreter);
//			vi. Throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
		}

//		c. If preferredType is not present, let preferredType be number.
//		d. Return ? OrdinaryToPrimitive(input, preferredType).
		return ordinaryToPrimitive(interpreter, preferredType == null ? Type.Number : preferredType);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-ordinarytoprimitive")
	private Primitive<?> ordinaryToPrimitive(Interpreter interpreter, Type hint) throws AbruptCompletion {
		// 1. Assert: Type(O) is Object.
		// 2. Assert: hint is either string or number.
		assert hint == Type.String || hint == Type.Number;

		// 3. If hint is string, then Let methodNames be "toString", "valueOf".
		final String[] methodNames = hint == Type.String ?
			new String[] { "toString", "valueOf" } :
		// 4. Else, Let methodNames be "valueOf", "toString".
			new String[] { "valueOf", "toString" };

		// 5. For each element name of methodNames, do
		for (final String name : methodNames) {
			// a. Let method be ? Get(O, name).
			final Value<?> method = get(new StringLiteral(name));
			// b. If IsCallable(method) is true, then
			if (method instanceof final Executable<?> executable) {
				// i. Let result be ? Call(method, O).
				final Value<?> result = executable.call(interpreter, this);
				// ii. If Type(result) is not Object, return result.
				if (result.type != Type.Dictionary) return (Primitive<?>) result;
			}
		}

		// 6. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, Type.String).toStringLiteral(interpreter);
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
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

	public void set(Interpreter interpreter, StringLiteral key, Value<?> value) throws AbruptCompletion {
		if (this.value.get(key) instanceof final NativeProperty property) {
			property.value.set(interpreter, value);
		} else {
			this.value.put(key, value);
		}
	}

	public void put(String key, Value<?> value) {
		this.value.put(new StringLiteral(key), value);
	}

	public void put(Key<?> key, Value<?> value) {
		this.value.put(key, value);
	}

	public void setMethod(String name, NativeCode code) {
		this.value.put(new StringLiteral(name), new NativeFunction(code));
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
		return Undefined.instance;
		// throw new ExecutionError("Property '" + key.value + "' does not exist on object!");
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
	public void dump(int indent) {
		dumpRecursive(indent, new HashSet<>());
	}

	@SuppressWarnings("unchecked")
	protected void dumpRecursive(int indent, HashSet<Dictionary> parents) {
		Dumper.dumpName(indent, "Dictionary");
		if (value.isEmpty()) {
			Dumper.dumpIndent(indent + 1);
			System.out.print(ANSI.RED);
			System.out.print("[empty]");
			System.out.println(ANSI.RESET);
			return;
		}

		parents.add(this);
		for (var entry : value.entrySet()) {
			Dumper.dumpIndent(indent + 1);
			System.out.print(entry.getKey());
			System.out.print(": ");
			final Value<?> value = entry.getValue();
			if (value instanceof final Dictionary dictionary) {
				if (parents.contains(dictionary)) {
					System.out.print(ANSI.RED);
					System.out.print(this == value ? "[self]" : "[parent]");
					System.out.println(ANSI.RESET);
				} else {
					System.out.println();
					dictionary.dumpRecursive(indent + 2, (HashSet<Dictionary>) parents.clone());
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
		return UNIQUE_ID;
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "object";
	}

	@Override
	public void represent(StringRepresentation representation) {
		this.representRecursive(representation, new HashSet<>());
	}

	@SuppressWarnings("unchecked")
	public void representRecursive(StringRepresentation representation, HashSet<Dictionary> parents) {
		representation.append('{');
		if (value.isEmpty()) {
			representation.append(" }");
			return;
		}

		parents.add(this);

		representation.appendLine();
		representation.indent();

		for (var iterator = this.value.entrySet().iterator(); iterator.hasNext(); ) {
			final var entry = iterator.next();
			representation.appendIndent();
			representation.append(ANSI.YELLOW);
			representation.append(entry.getKey().value);
			representation.append(ANSI.RESET);
			representation.append(": ");
			final Value<?> value = entry.getValue();
			if (value instanceof final Dictionary dictionary) {
				if (parents.contains(dictionary)) {
					representation.append(ANSI.RED);
					representation.append(this == value ? "[self]" : "[parent]");
					representation.append(ANSI.RESET);
				} else {
					dictionary.representRecursive(representation, (HashSet<Dictionary>) parents.clone());
				}
			} else {
				value.represent(representation);
			}

			if (iterator.hasNext()) representation.append(',');
			representation.appendLine();
		}

		representation.unindent();
		representation.appendIndent();
		representation.append('}');
	}
}