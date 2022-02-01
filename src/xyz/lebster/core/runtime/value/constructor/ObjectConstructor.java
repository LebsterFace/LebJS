package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.NullValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-constructor")
public final class ObjectConstructor extends BuiltinConstructor<ObjectValue> {
	public static final ObjectConstructor instance = new ObjectConstructor();

	static {
		instance.putNonWritable(Names.prototype, ObjectPrototype.instance);

		instance.putMethod("setPrototypeOf", ObjectConstructor::setPrototypeOf);
		instance.putMethod("getPrototypeOf", ObjectConstructor::getPrototypeOf);
		instance.putMethod("create", ObjectConstructor::create);
	}

	private ObjectConstructor() {
		super();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.create")
	@NonCompliant
	private static Value<?> create(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Object.create ( O, Properties )
		final Value<?> O = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		return ObjectValue.createFromPrototype(O);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.setprototypeof")
	private static Value<?> setPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> O = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		final Value<?> proto = arguments.length > 1 ? arguments[1] : UndefinedValue.instance;

		// 1. Set O to ? RequireObjectCoercible(O).
		if (O.isNullish())
			throw AbruptCompletion.error(new TypeError("Object.setPrototypeOf called on null or undefined"));

		// 2. If Type(proto) is neither Object nor Null, throw a TypeError exception.
		ObjectValue proto_objectValue = null;
		if (proto instanceof final ObjectValue p) {
			proto_objectValue = p;
		} else if (proto != NullValue.instance) {
			throw AbruptCompletion.error(new TypeError("Object prototype may only be an Object or null"));
		}

		// 3. If Type(O) is not Object, return O.
		if (!(O instanceof final ObjectValue O_objectValue)) return O;
		// 4. Let status be ? O.[[SetPrototypeOf]](proto).
		final boolean status = O_objectValue.setPrototype(proto_objectValue);
		// 5. If status is false, throw a TypeError exception.
		if (!status)
			throw AbruptCompletion.error(new TypeError("Cyclic object prototype value"));
		// 6. Return O.
		return O;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.getprototypeof")
	private static Value<?> getPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> O = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		// 1. Let obj be ? ToObject(O).
		// 2. Return ? obj.[[GetPrototypeOf]]().
		final ObjectValue prototype = O.toObjectValue(interpreter).getPrototype();
		return prototype == null ? NullValue.instance : prototype;
	}

	@Override
	protected String getName() {
		return "Object";
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("Object()");
	}

	@Override
	public ObjectValue construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Object()");
	}
}
