package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-string-prototype-object")
public final class StringPrototype extends ObjectValue {
	public static final StringPrototype instance = new StringPrototype();

	static {
		instance.setMethod("reverse", StringPrototype::reverse);
		instance.setMethod("slice", StringPrototype::slice);
	}

	private StringPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.slice")
	private static StringValue slice(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// String.prototype.slice ( start, end )
		final Value<?> start = args.length > 0 ? args[0] : UndefinedValue.instance;
		final Value<?> end = args.length > 1 ? args[1] : UndefinedValue.instance;
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = interpreter.thisValue();
		if (O.isNullish())
			throw AbruptCompletion.error(new TypeError("String.prototype.slice called on null or undefined"));
		// 2. Let S be ? ToString(O).
		final var S = O.toStringValue(interpreter);
		// 3. Let len be the length of S.
		final var len = S.value.length();
		// 4. Let intStart be ? ToIntegerOrInfinity(start).
		int intStart = NumberPrototype.toIntegerOrInfinity(interpreter, start);
		// 5. If intStart is -∞, let from be 0.
		int from;
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
		int intEnd = end == UndefinedValue.instance ? len : NumberPrototype.toIntegerOrInfinity(interpreter, end);
		// 9. If intEnd is -∞, let to be 0.
		int to;
		if (intEnd == Integer.MIN_VALUE) {
			to = 0;
		}
		// 10. Else if intEnd < 0, let to be max(len + intEnd, 0).
		else if (intEnd < 0) {
			to = Math.max(len + intEnd, 0);
		}
		// 11. Else, let to be min(intEnd, len).
		else {
			to = Math.min(intEnd, len);
		}
		// 12. If from ≥ to, return the empty String.
		if (from >= to)
			return new StringValue("");
		// 13. Return the substring of S from from to to.
		return new StringValue(S.value.substring(from, to));
	}

	@NonStandard
	private static StringValue reverse(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		final String S = interpreter.thisValue().toStringValue(interpreter).value;
		return new StringValue(new StringBuilder(S).reverse().toString());
	}
}