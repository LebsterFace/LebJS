package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.CheckedError;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.native_.NativeCode;
import xyz.lebster.core.runtime.value.native_.NativeFunction;
import xyz.lebster.core.runtime.value.object.property.DataDescriptor;
import xyz.lebster.core.runtime.value.object.property.PropertyDescriptor;
import xyz.lebster.core.runtime.value.primitive.*;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

import java.util.*;

public class ObjectValue extends Value<Map<ObjectValue.Key<?>, PropertyDescriptor>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;
	private ObjectValue prototypeSlot = this.getDefaultPrototype();

	public ObjectValue() {
		super(new HashMap<>());
	}

	protected static ObjectValue createFromPrototype(Value<?> O) throws AbruptCompletion {
		ObjectValue prototype;
		if (O == Null.instance) {
			prototype = null;
		} else if (O instanceof final ObjectValue O_obj) {
			prototype = O_obj;
		} else {
			// 1. If Type(O) is neither Object nor Null, throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError("Object prototype may only be an Object or null"));
		}

		final var result = new ObjectValue();
		result.prototypeSlot = prototype;
		return result;
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
	public int hashCode() {
		return UNIQUE_ID;
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "object";
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public PrimitiveValue<?> toPrimitive(Interpreter interpreter, PreferredType preferredType) throws AbruptCompletion {
		// a. Let exoticToPrim be ? GetMethod(input, @@toPrimitive).
		final PropertyDescriptor exoticToPrim_property = this.getProperty(SymbolValue.toPrimitive);

		// b. If exoticToPrim is not undefined, then
		if (exoticToPrim_property != null) {
			final Executable<?> exoticToPrim = Executable.getExecutable(exoticToPrim_property.get(interpreter, this));
			final StringValue hint = preferredType == null ? Names.default_ :
				preferredType == PreferredType.String ? Names.string : Names.number;
			// iv. Let result be ? Call(exoticToPrim, input, hint).
			final Value<?> result = exoticToPrim.call(interpreter, this, hint);
			// v. If Type(result) is not Object, return result.
			if (!(result instanceof ObjectValue)) return result.toPrimitive(interpreter);
			// vi. Throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
		}

		// c. If preferredType is not present, let preferredType be number.
		// d. Return ? OrdinaryToPrimitive(input, preferredType).
		return ordinaryToPrimitive(interpreter, preferredType == null ? PreferredType.Number : preferredType);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage/#sec-ordinarytoprimitive")
	private PrimitiveValue<?> ordinaryToPrimitive(Interpreter interpreter, PreferredType hint) throws AbruptCompletion {
		// 1. Assert: Type(O) is Object.
		// 2. Assert: hint is either string or number.
		assert hint == PreferredType.String || hint == PreferredType.Number;

		// 3. If hint is string, then Let methodNames be "toString", "valueOf".
		final StringValue[] methodNames = hint == PreferredType.String ?
			new StringValue[] { Names.toString, Names.valueOf } :
			// 4. Else, Let methodNames be "valueOf", "toString".
			new StringValue[] { Names.valueOf, Names.toString };

		// 5. For each element name of methodNames, do
		for (final StringValue name : methodNames) {
			// a. Let method be ? Get(O, name).
			final Value<?> method = this.get(interpreter, name);
			// b. If IsCallable(method) is true, then
			if (method instanceof final Executable<?> executable) {
				// i. Let result be ? Call(method, O).
				final Value<?> result = executable.call(interpreter, this);
				// ii. If Type(result) is not Object, return result.
				if (result instanceof final PrimitiveValue<?> P) return P;
			}
		}

		// 6. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("Cannot convert object to primitive value"));
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, PreferredType.String).toStringValue(interpreter);
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, PreferredType.Number).toNumberValue(interpreter);
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return BooleanValue.TRUE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) {
		return this;
	}

	public boolean hasOwnProperty(Key<?> key) {
		return this.value.containsKey(key);
	}

	public PropertyDescriptor getOwnProperty(Key<?> key) {
		return this.value.get(key);
	}

	public final boolean hasProperty(Key<?> name) {
		if (this.hasOwnProperty(name)) return true;
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

	private PropertyDescriptor getProperty(Key<?> key) {
		ObjectValue object = this;

		while (object != null) {
			if (object.hasOwnProperty(key)) {
				// Property was found
				return object.getOwnProperty(key);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}

		// End of prototype chain; property does not exist.
		return null;
	}

	@NonCompliant
	public void set(Interpreter interpreter, Key<?> key, Value<?> value) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		if (property == null) {
			this.value.put(key, new DataDescriptor(value, true, true, true));
			return;
		}

		if (property.isWritable()) {
			property.set(interpreter, this, value);
		} else {
			final var representation = new StringRepresentation();
			representation.append("Cannot assign to read-only property ");
			key.display(representation);
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}
	}

	@NonStandard
	public Value<?> getWellKnownSymbolOrUndefined(Interpreter interpreter, SymbolValue key) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		return property == null ? Undefined.instance : property.get(interpreter, this);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getmethod")
	public Value<?> getMethod(Interpreter interpreter, SymbolValue P) throws AbruptCompletion {
		final Value<?> func = this.getWellKnownSymbolOrUndefined(interpreter, P);
		// 2. If func is either undefined or null, return undefined.
		if (func == Undefined.instance || func == Null.instance)
			return Undefined.instance;
		// 3. If IsCallable(func) is false, throw a TypeError exception.
		if (!(func instanceof Executable<?>))
			throw AbruptCompletion.error(new TypeError("Not a function!"));
		// 4. Return func.
		return func;
	}

	public final Value<?> get(Interpreter interpreter, Key<?> key) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		if (property == null) {
			if (interpreter.isCheckedMode()) {
				final var representation = new StringRepresentation();
				representation.append("Property ");
				key.displayForObjectKey(representation);
				representation.append(" does not exist on object.");
				throw AbruptCompletion.error(new CheckedError(representation.toString()));
			} else {
				return Undefined.instance;
			}
		}

		return property.get(interpreter, this);
	}

	public void put(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, true, true, true));
	}

	public void put(String key, Value<?> value) {
		this.value.put(new StringValue(key), new DataDescriptor(value, true, true, true));
	}

	public void putNonWritable(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, false, true, true));
	}

	public void putMethod(StringValue name, NativeCode code) {
		this.value.put(name, new DataDescriptor(new NativeFunction(name, code), true, true, true));
	}

	public void putMethod(SymbolValue name, NativeCode code) {
		this.value.put(name, new DataDescriptor(new NativeFunction(name, code), true, true, true));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerableownpropertynames")
	// The spec defines these three methods in EnumerableOwnPropertyNames
	// They have been split for clarity
	public StringValue[] enumerableOwnKeys() {
		// 2. Let properties be a new empty List.
		final List<StringValue> properties = new ArrayList<>();
		// 1. Let ownKeys be ? O.[[OwnPropertyKeys]]().
		// 3. For each element key of ownKeys, do
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : this.value.entrySet()) {
			// a. If Type(key) is String, then
			if (entry.getKey() instanceof final StringValue key_string) {
				// i. Let desc be ? O.[[GetOwnProperty]](key).
				// ii. If desc is not undefined and desc.[[Enumerable]] is true, then
				if (entry.getValue().isEnumerable()) {
					// 1. If kind is key, append key to properties.
					properties.add(key_string);
				}
			}
		}

		// 4. Return properties.
		return properties.toArray(new StringValue[0]);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerableownpropertynames")
	public Value<?>[] enumerableOwnValues(Interpreter interpreter) throws AbruptCompletion {
		// 2. Let properties be a new empty List.
		final List<Value<?>> properties = new ArrayList<>();
		// 1. Let ownKeys be ? O.[[OwnPropertyKeys]]().
		// 3. For each element key of ownKeys, do
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : this.value.entrySet()) {
			// a. If Type(key) is String, then
			if (entry.getKey() instanceof StringValue) {
				// i. Let desc be ? O.[[GetOwnProperty]](key).
				// ii. If desc is not undefined and desc.[[Enumerable]] is true, then
				if (entry.getValue().isEnumerable()) {
					// a. Let value be ? Get(O, key).
					final Value<?> value = entry.getValue().get(interpreter, this);
					// b. If kind is value, append value to properties.
					properties.add(value);
				}
			}
		}

		// 4. Return properties.
		return properties.toArray(new Value[0]);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerableownpropertynames")
	public ArrayObject[] enumerableOwnEntries(Interpreter interpreter) throws AbruptCompletion {
		// 2. Let properties be a new empty List.
		final List<ArrayObject> properties = new ArrayList<>();
		// 1. Let ownKeys be ? O.[[OwnPropertyKeys]]().
		// 3. For each element key of ownKeys, do
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : this.value.entrySet()) {
			// a. If Type(key) is String, then
			if (entry.getKey() instanceof StringValue) {
				// i. Let desc be ? O.[[GetOwnProperty]](key).
				// ii. If desc is not undefined and desc.[[Enumerable]] is true, then
				if (entry.getValue().isEnumerable()) {
					// ii. Let entry be ! CreateArrayFromList(« key, value »).
					// iii. Append entry to properties.
					properties.add(new ArrayObject(entry.getKey(), entry.getValue().get(interpreter, this)));
				}
			}
		}

		// 4. Return properties.
		return properties.toArray(new ArrayObject[0]);
	}

	/** @return An {@link IteratorRecord} for the Object, or {@code null} if the object is not iterable. */
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public final IteratorRecord getIterator(Interpreter interpreter) throws AbruptCompletion {
		// b. Otherwise, set method to ? GetMethod(obj, @@iterator).
		final Value<?> method = this.get(interpreter, SymbolValue.iterator);
		// 3. Let iterator be ? Call(method, obj).
		if (!(method instanceof final Executable<?> result)) return null;
		final Value<?> iterator_V = result.call(interpreter, this);
		// 4. If Type(iterator) is not Object, throw a TypeError exception.
		if (!(iterator_V instanceof final ObjectValue iterator))
			throw AbruptCompletion.error(new TypeError("Result of the Symbol.iterator method is not an object"));
		// 5. Let nextMethod be ? GetV(iterator, "next").
		final Value<?> nextMethod = iterator.get(interpreter, Names.next);
		// 6. Let iteratorRecord be the Record { [[Iterator]]: iterator, [[NextMethod]]: nextMethod, [[Done]]: false }.
		// 7. Return iteratorRecord.
		return new IteratorRecord(iterator, nextMethod, false);
	}

	public final void objectValue__display(StringRepresentation representation) {
		final var singleLine = new StringRepresentation();
		this.displayRecursive(singleLine, new HashSet<>(), true);
		if (singleLine.length() < 72) {
			representation.append(singleLine);
			return;
		}

		this.displayRecursive(representation, new HashSet<>(), false);
	}

	public final void representClassName(StringRepresentation representation) {
		representation.append(ANSI.CYAN);
		representation.append(this.getClass().getSimpleName());
		representation.append(ANSI.RESET);
	}

	@SuppressWarnings("unchecked")
	public final void objectValue__displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		if (this.getClass() != ObjectValue.class) {
			this.representClassName(representation);
			representation.append(' ');
		}

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
			if (entry.getValue() instanceof final DataDescriptor dataDescriptor) {
				final Value<?> value = dataDescriptor.getRawValue();
				if (value instanceof final ObjectValue object) {
					if (parents.contains(object)) {
						representation.append(ANSI.RED);

						if (object.getClass() != ObjectValue.class) {
							object.representClassName(representation);
						} else {
							representation.append(value == this ? "[self]" : "[parent]");
						}

						representation.append(ANSI.RESET);
					} else {
						object.displayRecursive(representation, (HashSet<ObjectValue>) parents.clone(), singleLine);
					}
				} else {
					value.display(representation);
				}
			} else {
				representation.append(ANSI.CYAN);
				representation.append("[Getter/Setter]");
				representation.append(ANSI.RESET);
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

	@Override
	public void display(StringRepresentation representation) {
		this.objectValue__display(representation);
	}

	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.objectValue__displayRecursive(representation, parents, singleLine);
	}

	public record IteratorRecord(ObjectValue iterator, Value<?> nextMethod, boolean done) {
	}

	public static abstract class Key<R> extends PrimitiveValue<R> {
		public Key(R value) {
			super(value);
		}

		protected void displayForObjectKey(StringRepresentation representation) {
			this.display(representation);
		}
	}
}