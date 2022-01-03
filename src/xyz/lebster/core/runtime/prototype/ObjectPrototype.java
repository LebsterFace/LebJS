package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.runtime.ArrayObject;
import xyz.lebster.core.runtime.LanguageError;

public final class ObjectPrototype extends Dictionary {
	public static final ObjectPrototype instance = new ObjectPrototype();

	private ObjectPrototype() {
		// https://tc39.es/ecma262/multipage#sec-object.prototype.tostring
		this.setMethod("toString", ObjectPrototype::toStringMethod);

		// https://tc39.es/ecma262/multipage#sec-object.prototype.valueof
		this.setMethod("valueOf", (interpreter, arguments) -> interpreter.thisValue().toDictionary(interpreter));
	}

	private static StringLiteral toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. If the `this` value is undefined, return "[object Undefined]".
		if (interpreter.thisValue() == Undefined.instance) {
			return new StringLiteral("[object Undefined]");
		} else if (interpreter.thisValue() == Null.instance) {
			return new StringLiteral("[object Null]");
		} else {// 3. Let O be ! ToObject(this value).
			final Dictionary O = interpreter.thisValue().toDictionary(interpreter);
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
			final Value<?> tag = O.get(Symbol.toStringTag);
			// 16. If Type(tag) is not String, set tag to builtinTag.
			// 17. Return the string-concatenation of "[object ", tag, and "]".
			if (tag instanceof final StringLiteral stringLiteral) {
				return new StringLiteral("[object " + stringLiteral.value + "]");
			} else {
				return new StringLiteral("[object " + builtinTag + "]");
			}
		}
	}

	@Override
	public Dictionary getPrototype() {
		return null;
	}
}