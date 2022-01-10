package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.node.value.native_.NativeFunction;
import xyz.lebster.core.node.value.object.Executable;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.error.LanguageError;
import xyz.lebster.core.runtime.object.ArrayObject;
import xyz.lebster.core.runtime.object.StringWrapper;

public final class ObjectPrototype extends ObjectValue {
	public static final ObjectPrototype instance = new ObjectPrototype();
	public static final NativeFunction toStringMethod = new NativeFunction(ObjectPrototype::toStringMethod);

	static {
		instance.put(Names.toString, toStringMethod);
		instance.setMethod(Names.valueOf, ObjectPrototype::valueOf);
	}

	private ObjectPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object.prototype.tostring")
	public static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. If the `this` value is undefined, return "[object Undefined]".
		if (interpreter.thisValue() == UndefinedValue.instance) {
			return new StringValue("[object Undefined]");
		} else if (interpreter.thisValue() == NullValue.instance) {
			return new StringValue("[object Null]");
		} else {// 3. Let O be ! ToObject(this value).
			final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
			String builtinTag;// 4. Let isArray be ? IsArray(O).
			// 5. If isArray is true, let builtinTag be "Array".
			// TODO: Pattern matching for switch
			if (O instanceof ArrayObject) builtinTag = "Array";
				// 6. Else if O has a [[ParameterMap]] internal slot, let builtinTag be "Arguments".
				// 7. Else if O has a [[Call]] internal method, let builtinTag be "Function".
			else if (O instanceof Executable<?>) builtinTag = "Function";
				// 8. Else if O has an [[ErrorData]] internal slot, let builtinTag be "Error".
			else if (O instanceof LanguageError) builtinTag = "Error";
				// 9. Else if O has a [[BooleanData]] internal slot, let builtinTag be "Boolean".
				// 10. Else if O has a [[NumberData]] internal slot, let builtinTag be "Number".
				// 11. Else if O has a [[StringData]] internal slot, let builtinTag be "String".
			else if (O instanceof StringWrapper) builtinTag = "String";
				// 12. Else if O has a [[DateValue]] internal slot, let builtinTag be "Date".
				// 13. Else if O has a [[RegExpMatcher]] internal slot, let builtinTag be "RegExp".
				// 14. Else, let builtinTag be "Object".
				// FIXME: Date Objects, RegExp Objects, BooleanWrapper Objects, NumberWrapper Objects
			else builtinTag = "Object";

			// 15. Let tag be ? Get(O, @@toStringTag).
			final Value<?> tag = O.get(SymbolValue.toStringTag);
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
	private static Value<?> valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return interpreter.thisValue().toObjectValue(interpreter);
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return null;
	}
}