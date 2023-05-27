package xyz.lebster.core.value.object;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.object.ObjectValue.EnumerableOwnPropertyNamesKind.*;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-constructor")
public final class ObjectConstructor extends BuiltinConstructor<ObjectValue, ObjectPrototype> {
	public ObjectConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Object, 1);

		putMethod(intrinsics, Names.setPrototypeOf, 2, ObjectConstructor::setPrototypeOf);
		putMethod(intrinsics, Names.getPrototypeOf, 1, ObjectConstructor::getPrototypeOf);
		putMethod(intrinsics, Names.create, 2, ObjectConstructor::create);
		putMethod(intrinsics, Names.keys, 1, ObjectConstructor::keys);
		putMethod(intrinsics, Names.values, 1, ObjectConstructor::values);
		putMethod(intrinsics, Names.entries, 1, ObjectConstructor::entries);
		putMethod(intrinsics, Names.getOwnPropertyDescriptor, 2, ObjectConstructor::getOwnPropertyDescriptor);
		putMethod(intrinsics, Names.getOwnPropertyDescriptors, 1, ObjectConstructor::getOwnPropertyDescriptors);
		putMethod(intrinsics, Names.fromEntries, 1, ObjectConstructor::fromEntries);
		putMethod(intrinsics, Names.is, 2, ObjectConstructor::is);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.getownpropertydescriptors")
	private static ObjectValue getOwnPropertyDescriptors(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.9 Object.getOwnPropertyDescriptors ( O )
		final Value<?> O = argument(0, arguments);

		// 1. Let obj be ? ToObject(O).
		final ObjectValue obj = O.toObjectValue(interpreter);
		// 2. Let ownKeys be ? obj.[[OwnPropertyKeys]]().
		final Iterable<Key<?>> ownKeys = obj.ownPropertyKeys();
		// TODO: 3. Let descriptors be OrdinaryObjectCreate(%Object.prototype%).
		final var descriptors = new ObjectValue(interpreter.intrinsics);
		// 4. For each element key of ownKeys, do
		for (final Key<?> key : ownKeys) {
			// a. Let desc be ? obj.[[GetOwnProperty]](key).
			final PropertyDescriptor desc = obj.getOwnProperty(key);
			// b. Let descriptor be FromPropertyDescriptor(desc).
			final ObjectValue descriptor = desc.fromPropertyDescriptor(interpreter);
			// TODO: c. If descriptor is not undefined, perform ! CreateDataPropertyOrThrow(descriptors, key, descriptor).
			// TODO: When does fromPropertyDescriptor return 'undefined'?
			descriptors.put(key, descriptor);
		}
		// 5. Return descriptors.
		return descriptors;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.getownpropertydescriptor")
	private static Value<?> getOwnPropertyDescriptor(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.8 Object.getOwnPropertyDescriptor ( O, P )
		final Value<?> O = argument(0, arguments);
		final Value<?> P = argument(1, arguments);

		// 1. Let obj be ? ToObject(O).
		final ObjectValue obj = O.toObjectValue(interpreter);
		// 2. Let key be ? ToPropertyKey(P).
		final Key<?> key = P.toPropertyKey(interpreter);
		// 3. Let desc be ? obj.[[GetOwnProperty]](key).
		final PropertyDescriptor desc = obj.getOwnProperty(key);
		if (desc == null) return Undefined.instance;
		// 4. Return FromPropertyDescriptor(desc).
		return desc.fromPropertyDescriptor(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.is")
	private static BooleanValue is(Interpreter interpreter, Value<?>[] arguments) {
		// 20.1.2.14 Object.is ( value1, value2 )
		final Value<?> value1 = argument(0, arguments);
		final Value<?> value2 = argument(1, arguments);

		// 1. Return SameValue(value1, value2).
		return BooleanValue.of(value1.sameValue(value2));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.fromentries")
	@NonCompliant
	private static ObjectValue fromEntries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.7 Object.fromEntries ( iterable )
		final Value<?> iterable = argument(0, arguments);

		if (!(iterable instanceof ArrayObject arrayObject))
			throw error(new TypeError(interpreter, "Object.fromEntries call with non-array"));

		final ObjectValue result = new ObjectValue(interpreter.intrinsics);
		for (final PropertyDescriptor descriptor : arrayObject) {
			final Value<?> value = descriptor.get(interpreter, arrayObject);

			if (!(value instanceof final ObjectValue entry))
				throw error(new TypeError(interpreter, "Value is not an entry object"));

			final ObjectValue.Key<?> entryKey = entry.get(interpreter, new StringValue("0")).toPropertyKey(interpreter);
			final Value<?> entryValue = entry.get(interpreter, new StringValue("1"));
			result.put(entryKey, entryValue, true, true, true);
		}

		return result;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.keys")
	private static ArrayObject keys(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.18 Object.keys ( O )
		final Value<?> O = argument(0, arguments);

		// 1. Let obj be ? ToObject(O).
		final ObjectValue obj = O.toObjectValue(interpreter);
		// 2. Let nameList be ? EnumerableOwnPropertyNames(obj, key).
		final ArrayList<Value<?>> nameList = obj.enumerableOwnPropertyNames(interpreter, KEY);
		// 3. Return CreateArrayFromList(nameList).
		return new ArrayObject(interpreter, nameList);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.values")
	private static ArrayObject values(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.23 Object.values ( O )
		final Value<?> O = argument(0, arguments);

		// 1. Let obj be ? ToObject(O).
		final ObjectValue obj = O.toObjectValue(interpreter);
		// 2. Let nameList be ? EnumerableOwnPropertyNames(obj, value).
		final ArrayList<Value<?>> nameList = obj.enumerableOwnPropertyNames(interpreter, VALUE);
		// 3. Return CreateArrayFromList(nameList).
		return new ArrayObject(interpreter, nameList);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.entries")
	private static ArrayObject entries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.5 Object.entries ( O )
		final Value<?> O = argument(0, arguments);

		// 1. Let obj be ? ToObject(O).
		final ObjectValue obj = O.toObjectValue(interpreter);
		// 2. Let nameList be ? EnumerableOwnPropertyNames(obj, key+value).
		final ArrayList<Value<?>> nameList = obj.enumerableOwnPropertyNames(interpreter, KEY_VALUE);
		// 3. Return CreateArrayFromList(nameList).
		return new ArrayObject(interpreter, nameList);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.create")
	@NonCompliant
	private static Value<?> create(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Object.create ( O, Properties )
		final Value<?> O = argument(0, arguments);

		if (O == Null.instance) {
			return new ObjectValue(Null.instance);
		} else if (O instanceof final ObjectValue prototype) {
			return new ObjectValue(prototype);
		} else {
			// 1. If Type(O) is neither Object nor Null, throw a TypeError exception.
			throw error(new TypeError(interpreter, "Object prototype may only be an Object or null"));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.setprototypeof")
	private static Value<?> setPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.22 Object.setPrototypeOf ( O, proto )
		final Value<?> O = argument(0, arguments);
		final Value<?> proto = argument(1, arguments);

		// 1. Set O to ? RequireObjectCoercible(O).
		if (O.isNullish())
			throw error(new TypeError(interpreter, "Object.setPrototypeOf called on null or undefined"));

		// 2. If Type(proto) is neither Object nor Null, throw a TypeError exception.
		if (!(proto instanceof ObjectValue || proto == Null.instance))
			throw error(new TypeError(interpreter, "Object prototype may only be an Object or null"));

		// 3. If Type(O) is not Object, return O.
		if (!(O instanceof final ObjectValue O_objectValue)) return O;
		// 4. Let status be ? O.[[SetPrototypeOf]](proto).
		final boolean status = O_objectValue.setPrototype(proto);
		// 5. If status is false, throw a TypeError exception.
		if (!status)
			throw error(new TypeError(interpreter, "Cyclic object prototype value"));
		// 6. Return O.
		return O;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.getprototypeof")
	private static Value<?> getPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.12 Object.getPrototypeOf ( O )
		final Value<?> O = argument(0, arguments);

		// 1. Let obj be ? ToObject(O).
		// 2. Return ? obj.[[GetPrototypeOf]]().
		final ObjectValue prototype = O.toObjectValue(interpreter).getPrototype();
		return prototype == null ? Null.instance : prototype;
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-value")
	public ObjectValue internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.1.1.1 Object ( [ value ] )
		final Value<?> value = argument(0, arguments);

		// 2. If value is undefined or null, return OrdinaryObjectCreate(%Object.prototype%).
		if (value.isNullish()) return new ObjectValue(interpreter.intrinsics);
		// 3. Return ! ToObject(value).
		return value.toObjectValue(interpreter);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-constructor")
	public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		// 20.1.1.1 Object ( [ value ] )
		final Value<?> value = argument(0, arguments);

		// FIXME: 1. If NewTarget is neither undefined nor the active function, then
		//            a. Return ? OrdinaryCreateFromConstructor(NewTarget, "%Object.prototype%").
		// 2. If value is undefined or null, return OrdinaryObjectCreate(%Object.prototype%).
		if (value.isNullish()) return new ObjectValue(interpreter.intrinsics);
		// 3. Return ! ToObject(value).
		return value.toObjectValue(interpreter);
	}
}
