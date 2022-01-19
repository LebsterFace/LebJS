package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.native_.NativeCode;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.native_.NativeProperty;
import xyz.lebster.core.runtime.value.primitive.*;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ObjectValue extends Value<Map<ObjectValue.Key<?>, Value<?>>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;
	private ObjectValue prototypeSlot = this.getDefaultPrototype();

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
		if (preferredType == Value.Type.String) return "string";
			// iii. Else,
		else {
			// 1. Assert: preferredType is number.
			assert preferredType == Value.Type.Number;
			// 2. Let hint be "number".
			return "number";
		}
	}

	public ObjectValue getDefaultPrototype() {
		return ObjectPrototype.instance;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarygetprototypeof")
	public final ObjectValue getPrototype() {
		return this.prototypeSlot;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarysetprototypeof")
	public final boolean setPrototype(ObjectValue V) {
		// 1. Let current be O.[[Prototype]].
		final ObjectValue current = this.prototypeSlot;
		// 2. If SameValue(V, current) is true, return true.
		if (V.sameValue(current)) return true;

		// FIXME: [[Extensible]]
		// 3. Let extensible be O.[[Extensible]].
		// 4. If extensible is false, return false.

		// 5. Let p be V.
		ObjectValue p = V;
		// 6. Let done be false.
		boolean done = false;
		// 7. Repeat, while done is false,
		while (!done) {
			// a. If p is null, set done to true.
			if (p == null) {
				done = true;
			}
			// b. Else if SameValue(p, O) is true, return false.
			else if (p.sameValue(this)) {
				return false;
			}
			// c. Else,
			else {
				// FIXME: Custom [[GetPrototypeOf]]
				// i. If p.[[GetPrototypeOf]] is not the ordinary object internal method defined in 10.1.1, set done to true.
				// ii. Else, set p to p.[[Prototype]].
				p = p.getPrototype();
			}
		}

		// 8. Set O.[[Prototype]] to V.
		this.prototypeSlot = V;
		// 9. Return true.
		return true;
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public PrimitiveValue<?> toPrimitive(Interpreter interpreter, Type preferredType) throws AbruptCompletion {
		// a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
		final Value<?> exoticToPrim = get(interpreter, SymbolValue.toPrimitive);

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
		return ordinaryToPrimitive(interpreter, preferredType == null ? Value.Type.Number : preferredType);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-ordinarytoprimitive")
	private PrimitiveValue<?> ordinaryToPrimitive(Interpreter interpreter, Type hint) throws AbruptCompletion {
		// 1. Assert: Type(O) is Object.
		// 2. Assert: hint is either string or number.
		assert hint == Value.Type.String || hint == Value.Type.Number;

		// 3. If hint is string, then Let methodNames be "toString", "valueOf".
		final StringValue[] methodNames = hint == Value.Type.String ?
			new StringValue[] { Names.toString, Names.valueOf } :
			// 4. Else, Let methodNames be "valueOf", "toString".
			new StringValue[] { Names.valueOf, Names.toString };

		// 5. For each element name of methodNames, do
		for (final StringValue name : methodNames) {
			// a. Let method be ? Get(O, name).
			final Value<?> method = get(interpreter, name);
			// b. If IsCallable(method) is true, then
			if (method instanceof final Executable<?> executable) {
				// i. Let result be ? Call(method, O).
				final Value<?> result = executable.call(interpreter, this);
				// ii. If Type(result) is not Object, return result.
				if (result.type != Type.Object) return (PrimitiveValue<?>) result;
			}
		}

		// 6. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, Value.Type.String).toStringValue(interpreter);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, Value.Type.Number).toNumberValue(interpreter);
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

	public Value<?> get(Interpreter interpreter, Key<?> key) throws AbruptCompletion {
		ObjectValue object = this;

		while (object != null) {
			if (object.value.containsKey(key)) {
				// Property was found
				return object.value.get(key).getValue(interpreter);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}

		// End of prototype chain; property does not exist.
		return UndefinedValue.instance;
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

	@Override
	public int hashCode() {
		return UNIQUE_ID;
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "object";
	}

	@Override
	public void display(StringRepresentation representation) {
		final var singleLine = new StringRepresentation();
		this.displayRecursive(singleLine, new HashSet<>(), true);
		if (singleLine.length() < 72) {
			representation.append(singleLine);
			return;
		}

		this.displayRecursive(representation, new HashSet<>(), false);
	}

	@SuppressWarnings("unchecked")
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append("{ ");
		if (value.isEmpty()) {
			representation.append('}');
			return;
		}

		parents.add(this);
		if (!singleLine) {
			representation.appendLine();
			representation.indent();
		}

		for (var iterator = this.value.entrySet().iterator(); iterator.hasNext(); ) {
			final var entry = iterator.next();
			if (!singleLine) representation.appendIndent();
			representation.append(ANSI.BRIGHT_BLACK);
			entry.getKey().displayForObjectKey(representation);
			representation.append(ANSI.RESET);
			representation.append(": ");
			final Value<?> value = entry.getValue();
			if (value instanceof final ObjectValue object) {
				if (parents.contains(object)) {
					representation.append(ANSI.RED);
					representation.append(this == value ? "[self]" : "[parent]");
					representation.append(ANSI.RESET);
				} else {
					object.displayRecursive(representation, (HashSet<ObjectValue>) parents.clone(), singleLine);
				}
			} else {
				value.display(representation);
			}

			if (iterator.hasNext()) representation.append(',');
			if (singleLine) {
				representation.append(' ');
			} else {
				representation.appendLine();
			}
		}

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append('}');
	}

	public static abstract class Key<R> extends PrimitiveValue<R> {
		public Key(R value, Type type) {
			super(value, type);
		}

		protected void displayForObjectKey(StringRepresentation representation) {
			this.display(representation);
		}
	}
}