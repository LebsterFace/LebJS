package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.PrimitiveValue;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.error.CheckedError;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.function.NativeCode;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.number.NumberValue;
import xyz.lebster.core.value.string.StringValue;
import xyz.lebster.core.value.symbol.SymbolValue;

import java.util.*;

public class ObjectValue extends Value<Map<ObjectValue.Key<?>, PropertyDescriptor>> {
	private static int LAST_UNUSED_IDENTIFIER = 0;
	private final int UNIQUE_ID = ObjectValue.LAST_UNUSED_IDENTIFIER++;

	private ObjectValue prototypeSlot;

	private ObjectValue() {
		super(null);
		throw new ShouldNotHappen("Usage of private ObjectValue()");
	}

	public ObjectValue(ObjectValue prototype) {
		super(new HashMap<>());
		this.prototypeSlot = prototype;
	}

	@SuppressWarnings("unchecked")
	public static void staticDisplayRecursive(ObjectValue objectValue, StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		// TODO: Display class name if prototype is not Object.prototype
		if (objectValue.getClass() != ObjectValue.class) {
			objectValue.representClassName(representation);
			representation.append(' ');
		}

		representation.append('{');
		representation.append(' ');
		if (objectValue.value.isEmpty()) {
			representation.append('}');
			return;
		}

		parents.add(objectValue);
		if (!singleLine) {
			representation.appendLine();
			representation.indent();
		}

		final Iterator<Map.Entry<Key<?>, PropertyDescriptor>> iterator = objectValue.value.entrySet().iterator();
		while (iterator.hasNext()) {
			final Map.Entry<Key<?>, PropertyDescriptor> entry = iterator.next();
			if (!singleLine) representation.appendIndent();
			representation.append(ANSI.BRIGHT_BLACK);
			entry.getKey().displayForObjectKey(representation);
			representation.append(ANSI.RESET);
			representation.append(": ");

			entry.getValue().display(representation, objectValue, (HashSet<ObjectValue>) parents.clone(), singleLine);
			if (iterator.hasNext()) representation.append(',');
			if (singleLine) representation.append(' ');
			else representation.appendLine();
		}

		if (!singleLine) {
			representation.unindent();
			representation.appendIndent();
		}

		representation.append('}');
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
			throw AbruptCompletion.error(new TypeError(interpreter, "Cannot convert object to primitive value"));
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
		throw AbruptCompletion.error(new TypeError(interpreter, "Cannot convert object to primitive value"));
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
		final PropertyDescriptor property = this.getProperty(key);
		if (property == null) {
			this.defineOwnProperty(interpreter, key, new DataDescriptor(value, true, false, true));
			return;
		}

		if (property.isWritable()) {
			property.set(interpreter, this, value);
		} else {
			final var representation = new StringRepresentation();
			representation.append("Cannot assign to read-only property ");
			key.display(representation);
			throw AbruptCompletion.error(new TypeError(interpreter, representation.toString()));
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
		if (!(func instanceof Executable))
			throw AbruptCompletion.error(new TypeError(interpreter, "Not a function!"));
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
				throw AbruptCompletion.error(new CheckedError(interpreter, representation.toString()));
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
			throw AbruptCompletion.error(new TypeError(interpreter, "Cannot assign to read only property '" + P.toDisplayString() + "' of object"));
		}
		// 3. Return unused.
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinarydelete")
	private boolean ordinaryDelete(ObjectValue.Key<?> P) {
		// 10.1.10.1 OrdinaryDelete ( O, P )
		// 1. Let desc be ? O.[[GetOwnProperty]](P).
		final PropertyDescriptor desc = this.getOwnProperty(P);
		// 2. If desc is undefined, return true.
		if (desc == null) return true;
		// 3. If desc.[[Configurable]] is true, then
		if (desc.isConfigurable()) {
			// a. Remove the own property with name P from O.
			this.value.remove(P);
			// b. Return true.
			return true;
		}

		// 4. Return false.
		return false;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ordinary-object-internal-methods-and-internal-slots-delete-p")
	public boolean delete(ObjectValue.Key<?> P) {
		// 10.1.10 [[Delete]] ( P )
		// 1. Return ? OrdinaryDelete(O, P).
		return this.ordinaryDelete(P);
	}

	public void put(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, true, false, true));
	}

	public void putEnumerable(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, true, true, true));
	}

	public void putNonWritable(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, false, false, true));
	}

	protected void putFrozen(Key<?> key, Value<?> value) {
		this.value.put(key, new DataDescriptor(value, false, false, false));
	}

	public NativeFunction putMethod(FunctionPrototype functionPrototype, ObjectValue.Key<?> key, NativeCode code) {
		final var function = new NativeFunction(functionPrototype, key.toFunctionName(), code);
		this.put(key, function);
		return function;
	}

	public Iterable<Map.Entry<Key<?>, PropertyDescriptor>> entries() {
		return this.value.entrySet();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-enumerate-object-properties")
	public ArrayList<StringValue> enumerateObjectProperties() {
		final ArrayList<StringValue> result = new ArrayList<>();
		final HashSet<StringValue> visited = new HashSet<>();
		for (final var entry : entries()) {
			if (!(entry.getKey() instanceof StringValue key)) continue;
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

	public final void representClassName(StringRepresentation representation) {
		representation.append(ANSI.CYAN);
		representation.append(this.getClass().getSimpleName());
		representation.append(ANSI.RESET);
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
	}
}