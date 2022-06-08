package xyz.lebster.core.value.object;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;
import java.util.Map;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-constructor")
public final class ObjectConstructor extends BuiltinConstructor<ObjectValue, ObjectPrototype> {
	public ObjectConstructor(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype, fp, Names.Object);

		this.putMethod(fp, Names.setPrototypeOf, ObjectConstructor::setPrototypeOf);
		this.putMethod(fp, Names.getPrototypeOf, ObjectConstructor::getPrototypeOf);
		this.putMethod(fp, Names.create, ObjectConstructor::create);
		this.putMethod(fp, Names.keys, ObjectConstructor::keys);
		this.putMethod(fp, Names.values, ObjectConstructor::values);
		this.putMethod(fp, Names.entries, ObjectConstructor::entries);
		this.putMethod(fp, Names.fromEntries, ObjectConstructor::fromEntries);
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.fromentries")
	@NonCompliant
	private static ObjectValue fromEntries(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> arg = NativeFunction.argument(0, arguments);
		if (!(arg instanceof ArrayObject arrayObject))
			throw AbruptCompletion.error(new TypeError(interpreter, "Object.fromEntries call with non-array"));

		final ObjectValue result = new ObjectValue(interpreter.intrinsics.objectPrototype);
		for (final PropertyDescriptor descriptor : arrayObject) {
			final Value<?> value = descriptor.get(interpreter, arrayObject);

			if (!(value instanceof final ObjectValue entry))
				throw AbruptCompletion.error(new TypeError(interpreter, "Value is not an entry object"));

			final ObjectValue.Key<?> entryKey = entry.get(interpreter, new StringValue("0")).toPropertyKey(interpreter);
			final Value<?> entryValue = entry.get(interpreter, new StringValue("1"));
			result.putEnumerable(entryKey, entryValue);
		}

		return result;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.keys")
	private static ArrayObject keys(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final ObjectValue obj = argument(0, args).toObjectValue(interpreter);
		final ArrayList<StringValue> properties = new ArrayList<>();
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : obj.entries()) {
			if (entry.getValue().isEnumerable() && entry.getKey() instanceof final StringValue key) {
				properties.add(key);
			}
		}

		return new ArrayObject(interpreter, properties.toArray(new StringValue[0]));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.values")
	private static ArrayObject values(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final ObjectValue obj = argument(0, args).toObjectValue(interpreter);
		final ArrayList<Value<?>> properties = new ArrayList<>();
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : obj.entries()) {
			if (entry.getValue().isEnumerable() && entry.getKey() instanceof StringValue) {
				properties.add(entry.getValue().get(interpreter, obj));
			}
		}

		return new ArrayObject(interpreter, properties.toArray(new Value[0]));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.entries")
	private static ArrayObject entries(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final ObjectValue obj = argument(0, args).toObjectValue(interpreter);
		final ArrayList<ArrayObject> properties = new ArrayList<>();
		for (final Map.Entry<Key<?>, PropertyDescriptor> entry : obj.entries()) {
			if (entry.getValue().isEnumerable() && entry.getKey() instanceof StringValue) {
				properties.add(new ArrayObject(interpreter,
					entry.getKey(),
					entry.getValue().get(interpreter, obj)
				));
			}
		}

		return new ArrayObject(interpreter, properties.toArray(new ArrayObject[0]));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.create")
	@NonCompliant
	private static Value<?> create(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Object.create ( O, Properties )
		final Value<?> O = argument(0, arguments);

		if (O == Null.instance) {
			return new ObjectValue(null);
		} else if (O instanceof final ObjectValue prototype) {
			return new ObjectValue(prototype);
		} else {
			// 1. If Type(O) is neither Object nor Null, throw a TypeError exception.
			throw AbruptCompletion.error(new TypeError(interpreter, "Object prototype may only be an Object or null"));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.setprototypeof")
	private static Value<?> setPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.2.22 Object.setPrototypeOf ( O, proto )
		final Value<?> O = argument(0, arguments);
		final Value<?> proto = argument(1, arguments);

		// 1. Set O to ? RequireObjectCoercible(O).
		if (O.isNullish())
			throw AbruptCompletion.error(new TypeError(interpreter, "Object.setPrototypeOf called on null or undefined"));

		// 2. If Type(proto) is neither Object nor Null, throw a TypeError exception.
		if (!(proto instanceof ObjectValue || proto == Null.instance))
			throw AbruptCompletion.error(new TypeError(interpreter, "Object prototype may only be an Object or null"));

		// 3. If Type(O) is not Object, return O.
		if (!(O instanceof final ObjectValue O_objectValue)) return O;
		// 4. Let status be ? O.[[SetPrototypeOf]](proto).
		final boolean status = O_objectValue.setPrototype(proto);
		// 5. If status is false, throw a TypeError exception.
		if (!status)
			throw AbruptCompletion.error(new TypeError(interpreter, "Cyclic object prototype value"));
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
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 20.1.1.1 Object ( [ value ] )
		final Value<?> value = argument(0, arguments);
		// 2. If value is undefined or null, return OrdinaryObjectCreate(%Object.prototype%).
		if (value.isNullish()) return new ObjectValue(interpreter.intrinsics.objectPrototype);
		// 3. Return ! ToObject(value).
		return value.toObjectValue(interpreter);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-constructor")
	public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.1.1 Object ( [ value ] )
		final Value<?> value = argument(0, arguments);

		// FIXME: 1. If NewTarget is neither undefined nor the active function, then
		//            a. Return ? OrdinaryCreateFromConstructor(NewTarget, "%Object.prototype%").
		// 2. If value is undefined or null, return OrdinaryObjectCreate(%Object.prototype%).
		if (value.isNullish()) return new ObjectValue(interpreter.intrinsics.objectPrototype);
		// 3. Return ! ToObject(value).
		return value.toObjectValue(interpreter);
	}
}
