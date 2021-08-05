package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.TypeError;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.*;

public class Dictionary extends Value<Map<StringLiteral, Value<?>>> {
	public Dictionary(Map<StringLiteral, Value<?>> value) {
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

	public String toStringWithoutSideEffects() {
		final StringRepresentation representation = new StringRepresentation();
		this.represent(representation);
		return representation.toString();
	}

	@Override
	public Primitive<?> toPrimitive(Interpreter interpreter, Type preferredType) throws AbruptCompletion {
//		a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
		final Value<?> exoticToPrim = get(new StringLiteral("toPrimitive")); // FIXME: Symbol.toPrimitive

//		b. If exoticToPrim is not undefined, then
		if (exoticToPrim instanceof final Executable<?> executable) {
//			i - iii. Get hint
			final StringLiteral hint = new StringLiteral(getHint(preferredType));
//			iv. Let result be ? Call(exoticToPrim, input, hint).
			final ExecutionContext context = new ExecutionContext(interpreter.lexicalEnvironment(), executable, this);
			final Value<?> result = executable.callWithContext(interpreter, context, hint);
//			v. If Type(result) is not Object, return result.
			if (result.type != Type.Dictionary) return (Primitive<?>) result;
//			vi. Throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
		}

//		c. If preferredType is not present, let preferredType be number.
//		d. Return ? OrdinaryToPrimitive(input, preferredType).
		return ordinaryToPrimitive(interpreter, preferredType == null ? Type.Number : preferredType);
	}

	private Primitive<?> ordinaryToPrimitive(Interpreter interpreter, Type hint) throws AbruptCompletion {
//		1. Assert: Type(O) is Object.
//		2. Assert: hint is either string or number.
		assert hint == Type.String || hint == Type.Number;

//		3. If hint is string, then
//			a. Let methodNames be "toString", "valueOf".
//		4. Else,
//			a. Let methodNames be "valueOf", "toString".
		final String[] methodNames = hint == Type.String ?
			new String[] { "toString", "valueOf" } :
			new String[] { "valueOf", "toString" };

//		5. For each element name of methodNames, do
		for (final String name : methodNames) {
//			a. Let method be ? Get(O, name).
			final Value<?> method = get(new StringLiteral(name));
//			b. If IsCallable(method) is true, then
			if (method instanceof final Executable<?> executable) {
//				i. Let result be ? Call(method, O).
				final ExecutionContext context = new ExecutionContext(interpreter.lexicalEnvironment(), executable, this);
				final Value<?> result = executable.callWithContext(interpreter, context, new StringLiteral(getHint(hint)));
//				ii. If Type(result) is not Object, return result.
				if (result.type != Type.Dictionary) return (Primitive<?>) result;
			}
		}

//		6. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
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
	public String toString(Interpreter interpreter) throws AbruptCompletion {
		final Primitive<?> primValue = toPrimitive(interpreter, Type.String);
		return primValue.toString(interpreter);
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
		this.representRecursive(representation, new LinkedList<>());
	}

	protected void representRecursive(StringRepresentation representation, List<Dictionary> parents) {
		representation.append('{');
		if (value.isEmpty()) {
			representation.append(" }");
			return;
		}

		parents.add(this);

		representation.appendLine();
		representation.indent();

		final Set<Map.Entry<StringLiteral, Value<?>>> entrySet = this.value.entrySet();

		for (Iterator<Map.Entry<StringLiteral, Value<?>>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
			final Map.Entry<StringLiteral, Value<?>> entry = iterator.next();
			representation.appendIndent();
			representation.append(entry.getKey().value);
			representation.append(": ");
			final Value<?> value = entry.getValue();
			if (value instanceof final Dictionary dictionary) {
				if (parents.contains(dictionary)) {
					representation.append(ANSI.RED);
					representation.append(this == value ? "[self]" : "[parent]");
					representation.append(ANSI.RESET);
				} else {
					dictionary.representRecursive(representation, parents);
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