package xyz.lebster.core.node.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.TypeError;
import xyz.lebster.core.runtime.prototype.ObjectPrototype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ObjectValue extends Value<Map<ObjectValue.Key<?>, Value<?>>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;

	public ObjectValue(Map<Key<?>, Value<?>> value) {
		super(value, Type.Object);
	}

	public ObjectValue() {
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
		// a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
		final Value<?> exoticToPrim = get(Symbol.toPrimitive);

		// b. If exoticToPrim is not undefined, then
		if (exoticToPrim instanceof final Executable<?> executable) {
			// i - iii. Get hint
			final StringValue hint = new StringValue(getHint(preferredType));
			// iv. Let result be ? Call(exoticToPrim, input, hint).
			final Value<?> result = executable.call(interpreter, this, hint);
			// v. If Type(result) is not Object, return result.
			if (result.type != Type.Object) return result.toPrimitive(interpreter);
			// vi. Throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
		}

		// c. If preferredType is not present, let preferredType be number.
		// d. Return ? OrdinaryToPrimitive(input, preferredType).
		return ordinaryToPrimitive(interpreter, preferredType == null ? Type.Number : preferredType);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-ordinarytoprimitive")
	private Primitive<?> ordinaryToPrimitive(Interpreter interpreter, Type hint) throws AbruptCompletion {
		// 1. Assert: Type(O) is Object.
		// 2. Assert: hint is either string or number.
		assert hint == Type.String || hint == Type.Number;

		// 3. If hint is string, then Let methodNames be "toString", "valueOf".
		final StringValue[] methodNames = hint == Type.String ?
			new StringValue[] { ObjectPrototype.toString, ObjectPrototype.valueOf } :
			// 4. Else, Let methodNames be "valueOf", "toString".
			new StringValue[] { ObjectPrototype.valueOf, ObjectPrototype.toString };

		// 5. For each element name of methodNames, do
		for (final StringValue name : methodNames) {
			// a. Let method be ? Get(O, name).
			final Value<?> method = get(name);
			// b. If IsCallable(method) is true, then
			if (method instanceof final Executable<?> executable) {
				// i. Let result be ? Call(method, O).
				final Value<?> result = executable.call(interpreter, this);
				// ii. If Type(result) is not Object, return result.
				if (result.type != Type.Object) return (Primitive<?>) result;
			}
		}

		// 6. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, Type.String).toStringValue(interpreter);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, Type.Number).toNumberValue(interpreter);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return BooleanValue.TRUE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) {
		return this;
	}

	public void set(Interpreter interpreter, Key<?> key, Value<?> value) throws AbruptCompletion {
		if (this.value.get(key) instanceof final NativeProperty property) {
			property.value.set(interpreter, value);
		} else {
			this.value.put(key, value);
		}
	}

	public void put(String key, Value<?> value) {
		this.value.put(new StringValue(key), value);
	}

	public void put(Key<?> key, Value<?> value) {
		this.value.put(key, value);
	}

	public void setMethod(StringValue name, NativeCode code) {
		this.value.put(name, new NativeFunction(code));
	}

	public void setMethod(String name, NativeCode code) {
		this.value.put(new StringValue(name), new NativeFunction(code));
	}

	public Value<?> get(Key<?> key) {
		ObjectValue object = this;

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
	}

	public boolean hasProperty(Key<?> name) {
		if (hasOwnProperty(name)) return true;
		ObjectValue object = this;

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

	public boolean hasOwnProperty(Key<?> key) {
		return this.value.containsKey(key);
	}

	public ObjectValue getPrototype() {
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
	public void representRecursive(StringRepresentation representation, HashSet<ObjectValue> parents) {
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
			if (value instanceof final ObjectValue object) {
				if (parents.contains(object)) {
					representation.append(ANSI.RED);
					representation.append(this == value ? "[self]" : "[parent]");
					representation.append(ANSI.RESET);
				} else {
					object.representRecursive(representation, (HashSet<ObjectValue>) parents.clone());
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

	public static abstract class Key<R> extends Primitive<R> {
		public Key(R value, Type type) {
			super(value, type);
		}
	}
}