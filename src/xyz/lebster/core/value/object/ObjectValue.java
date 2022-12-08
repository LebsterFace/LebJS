package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.CheckedError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.function.NativeCode;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.*;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public class ObjectValue extends Value<Map<ObjectValue.Key<?>, PropertyDescriptor>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;

	private ObjectValue prototypeSlot;

	public ObjectValue(ObjectValue prototype) {
		super(new HashMap<>());
		this.prototypeSlot = prototype;
	}

	public ObjectValue(Intrinsics intrinsics) {
		this(intrinsics.objectPrototype);
	}

	public static void staticDisplayRecursive(ObjectValue objectValue, StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		if (objectValue.prototypeSlot == null) {
			representation.append(ANSI.CYAN);
			representation.append('[');
			representation.append(objectValue.getClass().getSimpleName());
			representation.append(": ");
			representation.append(ANSI.BRIGHT_WHITE);
			representation.append("null");
			representation.append(ANSI.CYAN);
			representation.append(" prototype]");
			representation.append(ANSI.RESET);
			representation.append(' ');
		} else if (
			// Avoid 'Object { }':
			!(objectValue.prototypeSlot instanceof ObjectPrototype) &&
			objectValue.prototypeSlot.getProperty(Names.constructor) instanceof final DataDescriptor constructorProperty &&
			constructorProperty.value() instanceof final ObjectValue constructor &&
			constructor.getProperty(Names.name) instanceof final DataDescriptor nameProperty &&
			nameProperty.value() instanceof final StringValue name
		) {
			representClassName(representation, name.value);
			representation.append(' ');
		} else if (objectValue.getClass() != ObjectValue.class) {
			representClassName(representation, objectValue.getClass().getSimpleName());
			representation.append(' ');
		}

		representation.append('{');
		if (objectValue.value.isEmpty()) {
			representation.append('}');
			return;
		}

		representation.append(' ');
		parents.add(objectValue);
		if (!singleLine) {
			representation.appendLine();
			representation.indent();
		}

		objectValue.representProperties(representation, parents, singleLine, objectValue.value.entrySet().iterator());

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append('}');
	}

	public static void representClassName(StringRepresentation representation, String className) {
		representation.append(ANSI.CYAN);
		representation.append(className);
		representation.append(ANSI.RESET);
	}

	protected static void representPropertyDelimiter(boolean moreElements, StringRepresentation representation, boolean singleLine) {
		if (moreElements) representation.append(',');
		if (singleLine) representation.append(' ');
		else representation.appendLine();
	}

	@SuppressWarnings("unchecked")
	protected void representProperties(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine, Iterator<Map.Entry<Key<?>, PropertyDescriptor>> iterator) {
		while (iterator.hasNext()) {
			final Map.Entry<Key<?>, PropertyDescriptor> entry = iterator.next();
			if (!singleLine) representation.appendIndent();
			representation.append(ANSI.BRIGHT_BLACK);
			entry.getKey().displayForObjectKey(representation);
			representation.append(ANSI.RESET);
			representation.append(": ");

			entry.getValue().display(representation, this, (HashSet<ObjectValue>) parents.clone(), singleLine);
			representPropertyDelimiter(iterator.hasNext(), representation, singleLine);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarygetprototypeof")
	public final ObjectValue getPrototype() {
		return this.prototypeSlot;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarysetprototypeof")
	public final boolean setPrototype(Value<?> V_or_null) {
		if (V_or_null == Null.instance) {
			this.prototypeSlot = null;
			return true;
		}

		if (!(V_or_null instanceof final ObjectValue V))
			throw new ShouldNotHappen("ObjectValue#getPrototypeOf called with non-object parameter");

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
			final Executable exoticToPrim = Executable.getExecutable(interpreter, exoticToPrim_property.get(interpreter, this));
			final StringValue hint = preferredType == null ? Names.default_ :
				preferredType == PreferredType.String ? Names.string : Names.number;
			// iv. Let result be ? Call(exoticToPrim, input, hint).
			final Value<?> result = exoticToPrim.call(interpreter, this, hint);
			// v. If Type(result) is not Object, return result.
			if (!(result instanceof ObjectValue)) return result.toPrimitive(interpreter);
			// vi. Throw a TypeError exception.
			throw error(new TypeError(interpreter, "Cannot convert object to primitive value"));
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
			if (method instanceof final Executable executable) {
				// i. Let result be ? Call(method, O).
				final Value<?> result = executable.call(interpreter, this);
				// ii. If Type(result) is not Object, return result.
				if (result instanceof final PrimitiveValue<?> P) return P;
			}
		}

		// 6. Throw a TypeError exception.
		throw error(new TypeError(interpreter, "Cannot convert object to primitive value"));
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

	public final boolean hasOwnEnumerableProperty(Key<?> key) {
		final PropertyDescriptor ownProperty = this.getOwnProperty(key);
		return ownProperty != null && ownProperty.isEnumerable();
	}

	public PropertyDescriptor getOwnProperty(Key<?> key) {
		return this.value.get(key);
	}

	public final boolean hasProperty(Key<?> name) {
		ObjectValue object = this;

		while (object != null) {
			if (object.hasOwnProperty(name)) {
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

	public final PropertyDescriptor getProperty(Key<?> key) {
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarydefineownproperty")
	@NonCompliant
	public boolean defineOwnProperty(Interpreter interpreter, Key<?> key, PropertyDescriptor descriptor) throws AbruptCompletion {
		this.value.put(key, descriptor);
		return true;
	}

	@NonCompliant
	// FIXME: `boolean throw` argument
	public void set(Interpreter interpreter, Key<?> key, Value<?> value) throws AbruptCompletion {
		final PropertyDescriptor property = this.getOwnProperty(key);
		if (property == null) {
			this.defineOwnProperty(interpreter, key, new DataDescriptor(value, true, true, true));
			return;
		}

		if (property.isWritable()) {
			property.set(interpreter, this, value);
		} else {
			final var representation = new StringRepresentation();
			representation.append("Cannot assign to read-only property ");
			key.display(representation);
			throw error(new TypeError(interpreter, representation.toString()));
		}
	}

	@NonStandard
	public Value<?> getWellKnownSymbolOrUndefined(Interpreter interpreter, SymbolValue key) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		return property == null ? Undefined.instance : property.get(interpreter, this);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getmethod")
	public Executable getMethod(Interpreter interpreter, SymbolValue P) throws AbruptCompletion {
		final Value<?> func = this.getWellKnownSymbolOrUndefined(interpreter, P);
		// 2. If func is either undefined or null, return undefined.
		if (func == Undefined.instance || func == Null.instance)
			return null;
		// 3. If IsCallable(func) is false, throw a TypeError exception.
		if (!(func instanceof final Executable func_executable))
			throw error(new TypeError(interpreter, "Not a function!"));
		// 4. Return func.
		return func_executable;
	}

	public final Value<?> get(Interpreter interpreter, Key<?> key) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		if (property == null) {
			if (interpreter.isCheckedMode()) {
				final var representation = new StringRepresentation();
				representation.append("Property ");
				key.displayForObjectKey(representation);
				representation.append(" does not exist on object.");
				throw error(new CheckedError(interpreter, representation.toString()));
			} else {
				return Undefined.instance;
			}
		}

		return property.get(interpreter, this);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-deletepropertyorthrow")
	public final void deletePropertyOrThrow(Interpreter interpreter, ObjectValue.Key<?> P) throws AbruptCompletion {
		// 7.3.10 DeletePropertyOrThrow ( O, P )
		// 1. Let success be ? O.[[Delete]](P).
		boolean success = this.delete(P);
		// 2. If success is false, throw a TypeError exception.
		if (!success) {
			throw error(new TypeError(interpreter, "Cannot assign to read only property '" + P.toDisplayString() + "' of object"));
		}
		// 3. Return unused.
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarydelete")
	private boolean ordinaryDelete(Key<?> P) {
		// 10.1.10.1 OrdinaryDelete ( O, P )
		// 1. Let desc be ? O.[[GetOwnProperty]](P).
		final PropertyDescriptor desc = this.getOwnProperty(P);
		// 2. If desc is undefined, return true.
		if (desc == null) return true;
		// 3. If desc.[[Configurable]] is true, then
		if (desc.isConfigurable()) {
			// a. Remove the own property with name P from O.
			this.internalDeleteProperty(P);
			// b. Return true.
			return true;
		}

		// 4. Return false.
		return false;
	}

	protected void internalDeleteProperty(Key<?> P) {
		this.value.remove(P);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinary-object-internal-methods-and-internal-slots-delete-p")
	public boolean delete(ObjectValue.Key<?> P) {
		// 10.1.10 [[Delete]] ( P )
		// 1. Return ? OrdinaryDelete(O, P).
		return this.ordinaryDelete(P);
	}

	public void put(Key<?> key, Value<?> value, boolean writable, boolean enumerable, boolean configurable) {
		this.value.put(key, new DataDescriptor(value, writable, enumerable, configurable));
	}

	public void put(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, true, false, true));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createbuiltinfunction")
	@NonCompliant
	protected NativeFunction putMethod(FunctionPrototype functionPrototype, Key<?> key, int expectedArgumentCount, NativeCode code) {
		final var function = new NativeFunction(functionPrototype, key.toFunctionName(), code, expectedArgumentCount);
		this.put(key, function);
		return function;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createbuiltinfunction")
	@NonCompliant
	public NativeFunction putMethod(Intrinsics intrinsics, Key<?> key, int expectedArgumentCount, NativeCode code) {
		return this.putMethod(intrinsics.functionPrototype, key, expectedArgumentCount, code);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinary-object-internal-methods-and-internal-slots-ownpropertykeys")
	public Iterable<Key<?>> ownPropertyKeys() {
		return ObjectValue.ordinaryOwnPropertyKeys(this);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryownpropertykeys")
	@NonCompliant
	// TODO: Follow specified order
	public static Iterable<Key<?>> ordinaryOwnPropertyKeys(ObjectValue objectValue) {
		return objectValue.value.keySet();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerate-object-properties")
	public final ArrayList<StringValue> enumerateObjectProperties() {
		final ArrayList<StringValue> result = new ArrayList<>();
		final HashSet<StringValue> visited = new HashSet<>();
		for (final var ownPropertyKey : ownPropertyKeys()) {
			if (!(ownPropertyKey instanceof StringValue key)) continue;
			final PropertyDescriptor desc = this.getOwnProperty(key);
			visited.add(key);
			if (desc.isEnumerable()) result.add(key);
		}

		final ObjectValue proto = this.getPrototype();
		if (proto == null) return result;

		for (final StringValue protoKey : proto.enumerateObjectProperties()) {
			if (!visited.contains(protoKey))
				result.add(protoKey);
		}

		return result;
	}

	protected enum EnumerableOwnPropertyNamesKind { KEY, VALUE, KEY_VALUE }

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerableownpropertynames")
	public final ArrayList<Value<?>> enumerableOwnPropertyNames(Interpreter interpreter, EnumerableOwnPropertyNamesKind kind) throws AbruptCompletion {
		// 1. Let ownKeys be ? O.[[OwnPropertyKeys]]().
		final Iterable<Key<?>> ownKeys = this.ownPropertyKeys();
		// 2. Let properties be a new empty List.
		final var properties = new ArrayList<Value<?>>();
		// 3. For each element key of ownKeys, do
		for (final Key<?> key_ : ownKeys) {
			// a. If key is a String, then
			if (!(key_ instanceof final StringValue key)) continue;

			// i. Let desc be ? O.[[GetOwnProperty]](key).
			final PropertyDescriptor desc = this.getOwnProperty(key);
			// ii. If desc is not undefined and desc.[[Enumerable]] is true, then
			if (desc != null && desc.isEnumerable()) {
				// 1. If kind is key, append key to properties.
				if (kind == EnumerableOwnPropertyNamesKind.KEY) properties.add(key);
				// 2. Else,
				else {
					// a. Let value be ? Get(O, key).
					final Value<?> value = this.get(interpreter, key);
					// b. If kind is value, append value to properties.
					if (kind == EnumerableOwnPropertyNamesKind.VALUE) properties.add(value);
					// c. Else,
					else {
						// i. Assert: kind is key+value.
						// ii. Let entry be CreateArrayFromList(« key, value »).
						final var entry = new ArrayObject(interpreter, key, value);
						// iii. Append entry to properties.
						properties.add(entry);
					}
				}
			}
		}

		// 4. Return properties.
		return properties;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-sortindexedproperties")
	public final ObjectValue sortIndexedProperties(Interpreter interpreter, long len, NativeCode SortCompare) throws AbruptCompletion {
		// 23.1.3.30.1 SortIndexedProperties ( obj, len, SortCompare )

		// 1. Let items be a new empty List.
		final ArrayList<Value<?>> items = new ArrayList<>();
		// 2. Let k be 0.
		int k = 0;
		// 3. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final var Pk = new StringValue(k);
			// b. Let kPresent be ? HasProperty(obj, Pk).
			final var kPresent = this.hasProperty(Pk);
			// c. If kPresent is true, then
			if (kPresent) {
				// i. Let kValue be ? Get(obj, Pk).
				final var kValue = this.get(interpreter, Pk);
				// ii. Append kValue to items.
				items.add(kValue);
			}

			// d. Set k to k + 1.
			k += 1;
		}

		// 4. Let itemCount be the number of elements in items.
		final int itemCount = items.size();
		// 5. Sort items using an implementation-defined sequence of calls to SortCompare.
		// If any such call returns an abrupt completion, stop before performing any further calls to SortCompare and return that Completion Record.
		try {
			items.sort((Value<?> a, Value<?> b) -> {
				Value<?> returnValue;
				try {
					returnValue = SortCompare.execute(interpreter, new Value[] { a, b });
				} catch (AbruptCompletion e) {
					if (e.type != AbruptCompletion.Type.Return) throw new RuntimeException(e);
					returnValue = e.value;
				}

				if (returnValue instanceof final NumberValue numberValue) {
					return (int) numberValue.value.doubleValue();
				} else {
					throw new ShouldNotHappen("SortCompare returned non-number");
				}
			});
		} catch (RuntimeException runtimeException) {
			if (runtimeException.getCause() instanceof final AbruptCompletion abruptCompletion) {
				throw abruptCompletion;
			} else {
				throw runtimeException;
			}
		}

		// 6. Let j be 0.
		int j = 0;
		// 7. Repeat, while j < itemCount,
		while (j < itemCount) {
			// a. Perform ? Set(obj, ! ToString(𝔽(j)), items[j], true).
			this.set(interpreter, new StringValue(j), items.get(j)/* FIXME: , true */);
			// b. Set j to j + 1.
			j += 1;
		}

		// 8. Repeat, while j < len,
		while (j < len) {
			// a. Perform ? DeletePropertyOrThrow(obj, ! ToString(𝔽(j))).
			this.deletePropertyOrThrow(interpreter, new StringValue(j));
			// b. Set j to j + 1.
			j += 1;
		}

		// 9. Return obj.
		return this;
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

	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		ObjectValue.staticDisplayRecursive(this, representation, parents, singleLine);
	}

	public static abstract class Key<R> extends PrimitiveValue<R> {
		public Key(R value) {
			super(value);
		}

		public void displayForObjectKey(StringRepresentation representation) {
			this.display(representation);
		}

		public abstract StringValue toFunctionName();

		public abstract int toIndex();

		public abstract boolean equalsKey(Key<?> other);
	}
}