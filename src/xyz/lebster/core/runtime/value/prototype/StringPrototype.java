package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.StringConstructor;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.StringWrapper;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import static xyz.lebster.core.runtime.value.prototype.NumberPrototype.toIntegerOrInfinity;
import static xyz.lebster.core.runtime.value.prototype.ObjectPrototype.requireObjectCoercible;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-string-prototype-object")
public final class StringPrototype extends ObjectValue {
	public static final StringPrototype instance = new StringPrototype();

	static {
		instance.put("constructor", StringConstructor.instance);
		instance.putMethod("reverse", StringPrototype::reverse);
		instance.putMethod("slice", StringPrototype::slice);
		instance.putMethod("charAt", StringPrototype::charAt);
		instance.putMethod(Names.valueOf, StringPrototype::valueOf);
		instance.putMethod(Names.toString, StringPrototype::toStringMethod);
	}

	private StringPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.charat")
	private static StringValue charAt(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// String.prototype.charAt ( pos )
		final Value<?> pos = args.length > 0 ? args[0] : Undefined.instance;
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.charAt");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let position be ? ToIntegerOrInfinity(pos).
		final int position = toIntegerOrInfinity(interpreter, pos);
		// 4. Let size be the length of S.
		final int size = S.value.length();
		// 5. If position < 0 or position ≥ size, return the empty String.
		if (position < 0 || position >= size)
			return new StringValue("");
		// 6. Return the substring of S from position to position + 1.
		return new StringValue(S.value.substring(position, position + 1));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.slice")
	private static StringValue slice(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// String.prototype.slice ( start, end )
		final Value<?> start = args.length > 0 ? args[0] : Undefined.instance;
		final Value<?> end = args.length > 1 ? args[1] : Undefined.instance;
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.slice");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let len be the length of S.
		final int len = S.value.length();
		// 4. Let intStart be ? ToIntegerOrInfinity(start).
		final int intStart = toIntegerOrInfinity(interpreter, start);
		// 5. If intStart is -∞, let from be 0.
		final int from;
		if (intStart == Integer.MIN_VALUE) {
			from = 0;
		}
		// 6. Else if intStart < 0, let from be max(len + intStart, 0).
		else if (intStart < 0) {
			from = Math.max(len + intStart, 0);
		}
		// 7. Else, let from be min(intStart, len).
		else {
			from = Math.min(intStart, len);
		}
		// 8. If end is undefined, let intEnd be len; else let intEnd be ? ToIntegerOrInfinity(end).
		final int intEnd = end == Undefined.instance ? len : toIntegerOrInfinity(interpreter, end);
		// 9. If intEnd is -∞, let `to` be 0.
		final int to;
		if (intEnd == Integer.MIN_VALUE) {
			to = 0;
		}
		// 10. Else if intEnd < 0, let `to` be max(len + intEnd, 0).
		else if (intEnd < 0) {
			to = Math.max(len + intEnd, 0);
		}
		// 11. Else, let `to` be min(intEnd, len).
		else {
			to = Math.min(intEnd, len);
		}
		// 12. If `from` ≥ `to`, return the empty String.
		if (from >= to)
			return new StringValue("");
		// 13. Return the substring of S from `from` to `to`.
		return new StringValue(S.value.substring(from, to));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.valueof")
	private static StringValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Return ? thisStringValue(this value).
		return thisStringValue(interpreter.thisValue());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Return ? thisStringValue(this value).
		return thisStringValue(interpreter.thisValue());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#thisstringvalue")
	private static StringValue thisStringValue(Value<?> value) throws AbruptCompletion {
		// 1. If Type(value) is String, return value.
		if (value instanceof final StringValue stringValue) return stringValue;
		// 2. If Type(value) is Object and value has a [[StringData]] internal slot, then
		if (value instanceof final StringWrapper stringWrapper) {
			// a. Let s be value.[[StringData]].
			// b. Assert: Type(s) is String.
			// c. Return s.
			return stringWrapper.data;
		}

		// 3. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("This method requires that 'this' be a String"));
	}

	@NonStandard
	private static StringValue reverse(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final String S = thisStringValue(interpreter.thisValue()).value;
		return new StringValue(new StringBuilder(S).reverse().toString());
	}
}