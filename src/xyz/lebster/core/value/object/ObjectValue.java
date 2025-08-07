package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.*;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.array.ArrayPrototype;
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
import java.util.Map.Entry;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public class ObjectValue extends Value<Map<Key<?>, PropertyDescriptor>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;
	private ObjectValue prototype;

	public ObjectValue(ObjectValue prototype) {
		super(new HashMap<>());
		this.prototype = prototype;
	}

	public ObjectValue(Null noPrototype) {
		this((ObjectValue) null);
	}

	public ObjectValue(Intrinsics intrinsics) {
		this(intrinsics.objectPrototype);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinaryownpropertykeys")
	@NonCompliant
	// TODO: Follow specified order
	public static Iterable<Key<?>> ordinaryOwnPropertyKeys(ObjectValue objectValue) {
		return objectValue.value.keySet();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarygetprototypeof")
	public final ObjectValue getPrototype() {
		return this.prototype;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarysetprototypeof")
	public final boolean setPrototype(Value<?> V_or_null) {
		if (V_or_null == Null.instance) {
			this.prototype = null;
			return true;
		}

		if (!(V_or_null instanceof final ObjectValue V))
			throw new ShouldNotHappen("ObjectValue#getPrototypeOf called with non-object parameter");

		// 1. Let current be O.[[Prototype]].
		final ObjectValue current = this.prototype;
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
		this.prototype = V;
		// 9. Return true.
		return true;
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public final int hashCode() {
		return UNIQUE_ID;
	}

	@Override
	public String typeOf() {
		return "object";
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public final PrimitiveValue<?> toPrimitive(Interpreter interpreter, PreferredType preferredType) throws AbruptCompletion {
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
	public final ObjectValue toObjectValue(Interpreter interpreter) {
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
	public final void set(Interpreter interpreter, Key<?> key, Value<?> value) throws AbruptCompletion {
		final PropertyDescriptor property = this.getOwnProperty(key);
		if (property == null) {
			this.defineOwnProperty(interpreter, key, new DataDescriptor(value, true, true, true));
			return;
		}

		if (property.isWritable()) {
			property.set(interpreter, this, value);
		} else {
			throw error(new TypeError(interpreter, "Cannot assign to read-only property %s".formatted(key.toDisplayString(true))));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getmethod")
	public final Executable getMethod(Interpreter interpreter, Key<?> P) throws AbruptCompletion {
		// 1. Let func be ? GetV(V, P).
		final Value<?> func = get(interpreter, P);
		// 2. If func is either undefined or null, return undefined.
		if (func.isNullish()) return null;
		// 3. If IsCallable(func) is false, throw a TypeError exception.
		if (!(func instanceof final Executable func_executable))
			throw error(new TypeError(interpreter, "Property %s is not a function".formatted(P.toDisplayString(true))));
		// 4. Return func.
		return func_executable;
	}

	public final Value<?> get(Interpreter interpreter, Key<?> key) throws AbruptCompletion {
		final PropertyDescriptor property = this.getProperty(key);
		if (property == null) return Undefined.instance;
		return property.get(interpreter, this);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-deletepropertyorthrow")
	public final void deletePropertyOrThrow(Interpreter interpreter, Key<?> P) throws AbruptCompletion {
		// 7.3.10 DeletePropertyOrThrow ( O, P )
		// 1. Let success be ? O.[[Delete]](P).
		boolean success = this.delete(P);
		// 2. If success is false, throw a TypeError exception.
		if (!success) {
			throw error(new TypeError(interpreter, "Property %s is non-configurable and can't be deleted".formatted(P.toDisplayString(true))));
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
	public final boolean delete(Key<?> P) {
		// 10.1.10 [[Delete]] ( P )
		// 1. Return ? OrdinaryDelete(O, P).
		return this.ordinaryDelete(P);
	}

	public final void put(Key<?> key, Value<?> value, boolean writable, boolean enumerable, boolean configurable) {
		this.value.put(key, new DataDescriptor(value, writable, enumerable, configurable));
	}

	public final void put(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, true, false, true));
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-createbuiltinfunction")
	protected final NativeFunction putMethod(FunctionPrototype functionPrototype, Key<?> key, int length, NativeCode code, boolean writable, boolean enumerable, boolean configurable) {
		final var function = new NativeFunction(functionPrototype, key.toFunctionName(), code, length);
		this.put(key, function, writable, enumerable, configurable);
		return function;
	}

	protected final NativeFunction putMethod(FunctionPrototype functionPrototype, Key<?> key, int length, NativeCode code) {
		return putMethod(functionPrototype, key, length, code, true, false, true);
	}

	public final NativeFunction putMethod(Intrinsics intrinsics, Key<?> key, int length, NativeCode code) {
		return putMethod(intrinsics.functionPrototype, key, length, code, true, false, true);
	}

	// TODO: Setter can return void?
	public final void putAccessor(Intrinsics intrinsics, Key<?> key, NativeCode getter, NativeCode setter, boolean enumerable, boolean configurable) {
		final String name = getter == null && setter == null ? null :
			key.toFunctionName().value;
		final NativeFunction getterFn = getter == null ? null :
			new NativeFunction(intrinsics, new StringValue("get " + name), getter, 0);
		final NativeFunction setterFn = setter == null ? null :
			new NativeFunction(intrinsics, new StringValue("set " + name), setter, 1);

		value.put(key, new AccessorDescriptor(getterFn, setterFn, enumerable, configurable));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinary-object-internal-methods-and-internal-slots-ownpropertykeys")
	public Iterable<Key<?>> ownPropertyKeys() {
		return ObjectValue.ordinaryOwnPropertyKeys(this);
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

	@Override
	public void display(StringBuilder builder) {
		if (displayAsJSON()) {
			JSONDisplayer.display(builder, this, false);
		} else {
			throw new NotImplemented("display() for " + getClass().getSimpleName());
		}
	}

	public boolean displayAsJSON() {
		return true;
	}

	public Iterable<Entry<Key<?>, PropertyDescriptor>> displayableProperties() {
		return value.entrySet();
	}

	public Iterable<Displayable> displayableValues() {
		return Collections.emptyList();
	}

	public void displayPrefix(StringBuilder builder) {
		builder.append(ANSI.CYAN);
		builder.append(getName());
		builder.append(ANSI.RESET);
	}

	private String getName() {
		if (this instanceof final HasBuiltinTag hbt) return hbt.getBuiltinTag();
		if (this.getClass() == ObjectValue.class) return "Object";
		return getClass().getSimpleName();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerableownproperties")
	public final List<Value<?>> enumerableOwnProperties(Interpreter interpreter, boolean keys, boolean values) throws AbruptCompletion {
		// 7.3.24 EnumerableOwnProperties ( O, kind )
		if (!keys && !values) throw new ShouldNotHappen("enumerableOwnProperties() called with neither keys nor values");

		// 1. Let ownKeys be ? O.[[OwnPropertyKeys]]().
		final Iterable<Key<?>> ownKeys = this.ownPropertyKeys();
		// 2. Let results be a new empty List.
		final ArrayList<Value<?>> results = new ArrayList<>();
		// 3. For each element key of ownKeys, do
		for (final Key<?> key_ : ownKeys) {
			// a. If key is a String, then
			if (!(key_ instanceof final StringValue key)) continue;
			// i. Let desc be ? O.[[GetOwnProperty]](key).
			final PropertyDescriptor desc = this.getOwnProperty(key);
			// ii. If desc is not undefined and desc.[[Enumerable]] is true, then
			if (desc == null || !desc.isEnumerable()) continue;
			// 1. If kind is key, then
			if (!values) {
				// a. Append key to results.
				results.add(key);
			}
			// 2. Else,
			else {
				// a. Let value be ? Get(O, key).
				final Value<?> value = this.get(interpreter, key);
				// b. If kind is value, then
				if (!keys) {
					// i. Append value to results.
					results.add(value);
				}
				// c. Else,
				else {
					// i. Assert: kind is key+value.
					// ii. Let entry be CreateArrayFromList(« key, value »).
					final var entry = new ArrayObject(interpreter, key, value);
					// iii. Append entry to results.
					results.add(entry);
				}
			}
		}

		// 4. Return results.
		return results;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-sortindexedproperties")
	public final List<Value<?>> sortIndexedProperties(Interpreter interpreter, int len, ArrayPrototype.ValueComparator SortCompare, boolean skipHoles) throws AbruptCompletion {
		// 23.1.3.30.1 SortIndexedProperties ( obj, len, SortCompare, holes )

		// 1. Let items be a new empty List.
		final ArrayList<Value<?>> items = new ArrayList<>();
		// 2. Let k be 0.
		int k = 0;
		// 3. Repeat, while k < len,
		while (k < len) {
			// a. Let Pk be ! ToString(𝔽(k)).
			final StringValue Pk = new StringValue(k);
			// b. If holes is skip-holes, then Let kRead be ? HasProperty(obj, Pk).
			// c. Else, Let kRead be true.
			final boolean kRead = !skipHoles || this.hasProperty(Pk);
			// d. If kRead is true, then
			if (kRead) {
				// i. Let kValue be ? Get(obj, Pk).
				final Value<?> kValue = this.get(interpreter, Pk);
				// ii. Append kValue to items.
				items.add(kValue);
			}

			// e. Set k to k + 1.
			k += 1;
		}

		// 4. Sort items using an implementation-defined sequence of calls to SortCompare.
		try {
			items.sort((x, y) -> {
				try {
					return SortCompare.compare(x, y);
				} catch (AbruptCompletion e) {
					if (e.type != AbruptCompletion.Type.Throw)
						throw new ShouldNotHappen("SortCompare gave " + e);

					// If any such call returns an abrupt completion,
					// stop before performing any further calls to SortCompare
					throw new RuntimeException(e);
				}
			});
		} catch (RuntimeException e) {
			if (e.getCause() instanceof final AbruptCompletion abruptCompletion) {
				// and return that Completion Record.
				throw abruptCompletion;
			} else {
				throw e;
			}
		}

		// 5. Return items.
		return items;
	}
}