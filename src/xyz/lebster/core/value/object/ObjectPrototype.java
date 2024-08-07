package xyz.lebster.core.value.object;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-object-prototype-object")
public final class ObjectPrototype extends ObjectValue {
	public NativeFunction toStringMethod;

	public ObjectPrototype() {
		super(Null.instance);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.tolocalestring")
	private static Value<?> toLocaleString(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.3.5 Object.prototype.toLocaleString ( [ reserved1 [ , reserved2 ] ] )

		// 1. Let O be the `this` value.
		// TODO: 2. Return ? Invoke(O, "toString").
		final Value<?> V = interpreter.thisValue();
		final ObjectValue O = V.toObjectValue(interpreter);
		// 1. If argumentsList is not present, set argumentsList to a new empty List.
		return Executable.getExecutable(interpreter, O.get(interpreter, Names.toString)).call(interpreter, V);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.isprototypeof")
	private static BooleanValue isPrototypeOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.3.3 Object.prototype.isPrototypeOf ( V )
		final Value<?> V_ = argument(0, arguments);

		// 1. If Type(V) is not Object, return false.
		if (!(V_ instanceof ObjectValue V)) return BooleanValue.FALSE;
		// 2. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 3. Repeat,
		while (true) {
			// a. Set V to ? V.[[GetPrototypeOf]]().
			V = V.getPrototype();
			// b. If V is null, return false.
			if (V == null) return BooleanValue.FALSE;
			// c. If SameValue(O, V) is true, return true.
			if (O.sameValue(V)) return BooleanValue.TRUE;
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.propertyisenumerable")
	private static BooleanValue propertyIsEnumerable(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.1.3.4 Object.prototype.propertyIsEnumerable ( V )
		final Value<?> V = argument(0, arguments);

		// 1. Let P be ? ToPropertyKey(V).
		final Key<?> P = V.toPropertyKey(interpreter);
		// 2. Let O be ? ToObject(this value).
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		// 3. Let desc be ? O.[[GetOwnProperty]](P).
		final PropertyDescriptor desc = O.getOwnProperty(P);
		// 4. If desc is undefined, return false.
		if (desc == null) return BooleanValue.FALSE;
		// 5. Return desc.[[Enumerable]].
		return BooleanValue.of(desc.isEnumerable());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-requireobjectcoercible")
	public static Value<?> requireObjectCoercible(Interpreter interpreter, Value<?> argument, String methodName) throws AbruptCompletion {
		if (argument.isNullish()) {
			// TODO: Use incompatibleReceiver
			throw error(new TypeError(interpreter, methodName + " called on null or undefined"));
		} else {
			return argument;
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.hasownproperty")
	private static BooleanValue hasOwnPropertyMethod(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// 20.1.3.2 Object.prototype.hasOwnProperty ( V )

		final ObjectValue object = interpreter.thisValue().toObjectValue(interpreter);
		final Key<?> property = args.length > 0 ? args[0].toPropertyKey(interpreter) : Names.undefined;
		return BooleanValue.of(object.hasOwnProperty(property));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.tostring")
	public static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. If the `this` value is undefined, return "[object Undefined]".
		if (interpreter.thisValue() == Undefined.instance) {
			return new StringValue("[object Undefined]");
		} else if (interpreter.thisValue() == Null.instance) {
			return new StringValue("[object Null]");
		} else {// 3. Let O be ! ToObject(this value).
			final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
			// 4. Let isArray be ? IsArray(O).
			// 5. If isArray is true, let builtinTag be "Array".
			// TODO: 6. Else if O has a [[ParameterMap]] internal slot, let builtinTag be "Arguments".
			// 7. Else if O has a [[Call]] internal method, let builtinTag be "Function".
			// 8. Else if O has an [[ErrorData]] internal slot, let builtinTag be "Error".
			// 9. Else if O has a [[BooleanData]] internal slot, let builtinTag be "Boolean".
			// 10. Else if O has a [[NumberData]] internal slot, let builtinTag be "Number".
			// 11. Else if O has a [[StringData]] internal slot, let builtinTag be "String".
			// TODO: 12. Else if O has a [[DateValue]] internal slot, let builtinTag be "Date".
			// 13. Else if O has a [[RegExpMatcher]] internal slot, let builtinTag be "RegExp".
			// 14. Else, let builtinTag be "Object".
			final String builtinTag = (O instanceof final HasBuiltinTag hbt) ? hbt.getBuiltinTag() : "Object";
			// 15. Let tag be ? Get(O, @@toStringTag).
			final Value<?> tag = O.get(interpreter, SymbolValue.toStringTag);
			// 16. If Type(tag) is not String, set tag to builtinTag.
			// 17. Return the string-concatenation of "[object ", tag, and "]".
			if (tag instanceof final StringValue stringValue) {
				return new StringValue("[object " + stringValue.value + "]");
			} else {
				return new StringValue("[object " + builtinTag + "]");
			}
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.valueof")
	private static ObjectValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Return ? ToObject(this value).
		return interpreter.thisValue().toObjectValue(interpreter);
	}

	@NonStandard
	private static BooleanValue hasPropertyMethod(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// Object.prototype.hasProperty(property: string | symbol): boolean

		final ObjectValue object = interpreter.thisValue().toObjectValue(interpreter);
		final Key<?> property = args.length > 0 ? args[0].toPropertyKey(interpreter) : Names.undefined;
		return BooleanValue.of(object.hasProperty(property));
	}

	public void populateMethods(Intrinsics intrinsics) {
		// 20.1.3.2 Object.prototype.hasOwnProperty ( V )
		putMethod(intrinsics, Names.hasOwnProperty, 1, ObjectPrototype::hasOwnPropertyMethod);
		// 20.1.3.3 Object.prototype.isPrototypeOf ( V )
		putMethod(intrinsics, Names.isPrototypeOf, 1, ObjectPrototype::isPrototypeOf);
		// 20.1.3.4 Object.prototype.propertyIsEnumerable ( V )
		putMethod(intrinsics, Names.propertyIsEnumerable, 1, ObjectPrototype::propertyIsEnumerable);
		// 20.1.3.5 Object.prototype.toLocaleString ( [ reserved1 [ , reserved2 ] ] )
		putMethod(intrinsics, Names.toLocaleString, 0, ObjectPrototype::toLocaleString);
		// 20.1.3.6 Object.prototype.toString ( )
		toStringMethod = putMethod(intrinsics, Names.toString, 0, ObjectPrototype::toStringMethod);
		// 20.1.3.7 Object.prototype.valueOf ( )
		putMethod(intrinsics, Names.valueOf, 0, ObjectPrototype::valueOf);

		// Non-standard
		putMethod(intrinsics, Names.hasProperty, 1, ObjectPrototype::hasPropertyMethod);
	}
}