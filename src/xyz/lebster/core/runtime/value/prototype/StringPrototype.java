package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.StringConstructor;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.StringWrapper;
import xyz.lebster.core.runtime.value.primitive.*;

import java.util.PrimitiveIterator;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;
import static xyz.lebster.core.runtime.value.prototype.NumberPrototype.toIntegerOrInfinity;
import static xyz.lebster.core.runtime.value.prototype.ObjectPrototype.requireObjectCoercible;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-string-prototype-object")
public final class StringPrototype extends ObjectValue {
	public static final StringPrototype instance = new StringPrototype();

	static {
		instance.put(Names.constructor, StringConstructor.instance);

		instance.putMethod(Names.reverse, StringPrototype::reverse);

		instance.putMethod(Names.slice, StringPrototype::slice);
		instance.putMethod(Names.charAt, StringPrototype::charAt);
		instance.putMethod(Names.trim, StringPrototype::trim);
		instance.putMethod(Names.trimStart, StringPrototype::trimStart);
		instance.putMethod(Names.trimEnd, StringPrototype::trimEnd);
		instance.putMethod(Names.toUpperCase, StringPrototype::toUpperCase);
		instance.putMethod(Names.toLowerCase, StringPrototype::toLowerCase);
		instance.putMethod(Names.valueOf, StringPrototype::valueOf);
		instance.putMethod(Names.toString, StringPrototype::toStringMethod);
		instance.putMethod(SymbolValue.iterator, StringIterator::new);
		instance.putMethod(Names.charCodeAt, StringPrototype::charCodeAt);
		instance.putMethod(Names.codePointAt, StringPrototype::codePointAt);
		instance.putMethod(Names.concat, StringPrototype::concat);
		instance.putMethod(Names.endsWith, StringPrototype::endsWith);
		instance.putMethod(Names.includes, StringPrototype::includes);
		instance.putMethod(Names.indexOf, StringPrototype::indexOf);
		instance.putMethod(Names.lastIndexOf, StringPrototype::lastIndexOf);
		instance.putMethod(Names.localeCompare, StringPrototype::localeCompare);
		instance.putMethod(Names.match, StringPrototype::match);
		instance.putMethod(Names.matchAll, StringPrototype::matchAll);
		instance.putMethod(Names.normalize, StringPrototype::normalize);
		instance.putMethod(Names.padEnd, StringPrototype::padEnd);
		instance.putMethod(Names.padStart, StringPrototype::padStart);
		instance.putMethod(Names.repeat, StringPrototype::repeat);
		instance.putMethod(Names.replace, StringPrototype::replace);
		instance.putMethod(Names.replaceAll, StringPrototype::replaceAll);
		instance.putMethod(Names.search, StringPrototype::search);
		instance.putMethod(Names.split, StringPrototype::split);
		instance.putMethod(Names.startsWith, StringPrototype::startsWith);
		instance.putMethod(Names.substring, StringPrototype::substring);
		instance.putMethod(Names.toLocaleLowerCase, StringPrototype::toLocaleLowerCase);
		instance.putMethod(Names.toLocaleUpperCase, StringPrototype::toLocaleUpperCase);
	}

	private StringPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.charcodeat")
	private static NumberValue charCodeAt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.3 String.prototype.charCodeAt ( pos )
		final Value<?> pos = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.charCodeAt");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let position be ? ToIntegerOrInfinity(pos).
		final int position = toIntegerOrInfinity(interpreter, pos);
		// 4. Let size be the length of S.
		final int size = S.length();
		// 5. If position < 0 or position ≥ size, return NaN.
		if (position < 0 || position >= size) return NumberValue.NaN;
		// 6. Return the Number value for the numeric value of the code unit at index position within the String S.
		return new NumberValue(S.charAt(position));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.codepointat")
	private static Value<?> codePointAt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.4 String.prototype.codePointAt ( pos )
		final Value<?> pos = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.codePointAt");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let position be ? ToIntegerOrInfinity(pos).
		final int position = toIntegerOrInfinity(interpreter, pos);
		// 4. Let size be the length of S.
		final int size = S.length();
		// 5. If position < 0 or position ≥ size, return undefined.
		if (position < 0 || position >= size) return Undefined.instance;
		// 6. Let cp be CodePointAt(S, position).
		// 7. Return 𝔽(cp.[[CodePoint]]).
		return new NumberValue(S.codePointAt(position));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.concat")
	private static StringValue concat(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		// 22.1.3.5 String.prototype.concat ( ...args )

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.concat");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let R be S.
		final StringBuilder R = new StringBuilder(S);
		// 4. For each element next of args, do
		for (final Value<?> next : args) {
			// a. Let nextString be ? ToString(next).
			final String nextString = next.toStringValue(interpreter).value;
			// b. Set R to the string-concatenation of R and nextString.
			R.append(nextString);
		}
		// 5. Return R.
		return new StringValue(R.toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.endswith")
	@NonCompliant
	// TODO: Use java.lang.String#endsWith
	private static BooleanValue endsWith(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.7 String.prototype.endsWith ( searchString [ , endPosition ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> endPosition = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.endsWith");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;

		// FIXME: 3. Let isRegExp be ? IsRegExp(searchString).
		//        4. If isRegExp is true, throw a TypeError exception.

		// 5. Let searchStr be ? ToString(searchString).
		final String searchStr = searchString.toStringValue(interpreter).value;
		// 6. Let len be the length of S.
		final int len = S.length();
		// 7. If endPosition is undefined, let pos be len; else let pos be ? ToIntegerOrInfinity(endPosition).
		int pos = endPosition == Undefined.instance ? len : toIntegerOrInfinity(interpreter, endPosition);
		// 8. Let end be the result of clamping pos between 0 and len.
		final int end = Math.max(0, Math.min(pos, len));
		// 9. Let searchLength be the length of searchStr.
		final int searchLength = searchStr.length();
		// 10. If searchLength = 0, return true.
		if (searchLength == 0) return BooleanValue.TRUE;
		// 11. Let start be end - searchLength.
		final int start = end - searchLength;
		// 12. If start < 0, return false.
		if (start < 0) return BooleanValue.FALSE;
		// 13. Let substring be the substring of S from start to end.
		final String substring = S.substring(start, end);
		// 14. Return SameValueNonNumeric(substring, searchStr).
		return BooleanValue.of(substring.equals(searchStr));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.includes")
	@NonCompliant
	private static BooleanValue includes(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.8 String.prototype.includes ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.includes");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;

		// FIXME: 3. Let isRegExp be ? IsRegExp(searchString).
		//        4. If isRegExp is true, throw a TypeError exception.

		// 5. Let searchStr be ? ToString(searchString).
		final String searchStr = searchString.toStringValue(interpreter).value;
		// 6. Let pos be ? ToIntegerOrInfinity(position).
		final int pos = toIntegerOrInfinity(interpreter, position);
		// 7. Assert: If position is undefined, then pos is 0.
		// 8. Let len be the length of S.
		final int len = S.length();
		// 9. Let start be the result of clamping pos between 0 and len.
		final int start = Math.max(0, Math.min(pos, len));
		// 10. Let index be StringIndexOf(S, searchStr, start).
		final int index = S.indexOf(searchStr, start);
		// 11. If index is not -1, return true.
		// 12. Return false.
		return BooleanValue.of(index != -1);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.indexof")
	private static NumberValue indexOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.9 String.prototype.indexOf ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.indexOf");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let searchStr be ? ToString(searchString).
		final String searchStr = searchString.toStringValue(interpreter).value;
		// 4. Let pos be ? ToIntegerOrInfinity(position).
		final int pos = toIntegerOrInfinity(interpreter, position);
		// 5. Assert: If position is undefined, then pos is 0.
		// 6. Let len be the length of S.
		final int len = S.length();
		// 7. Let start be the result of clamping pos between 0 and len.
		final int start = Math.max(0, Math.min(pos, len));
		// 8. Return 𝔽(StringIndexOf(S, searchStr, start)).
		return new NumberValue(S.indexOf(searchStr, start));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.lastindexof")
	private static NumberValue lastIndexOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.10 String.prototype.lastIndexOf ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.lastIndexOf");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let searchStr be ? ToString(searchString).
		final String searchStr = searchString.toStringValue(interpreter).value;
		// 4. Let numPos be ? ToNumber(position).
		final NumberValue numPos = position.toNumberValue(interpreter);
		// 5. Assert: If position is undefined, then numPos is NaN.
		// 6. If numPos is NaN, let pos be +∞; otherwise, let pos be ! ToIntegerOrInfinity(numPos).
		final int pos = numPos.value.isNaN() ? Integer.MAX_VALUE : toIntegerOrInfinity(interpreter, numPos);
		// 7. Let len be the length of S.
		final int len = S.length();
		// 8. Let start be the result of clamping pos between 0 and len.
		final int start = Math.max(0, Math.min(pos, len));
		// 9. If searchStr is the empty String, return 𝔽(start).
		if (searchStr.isEmpty()) return new NumberValue(start);
		// 10. Let searchLen be the length of searchStr.
		// 11. For each non-negative integer i starting with start such that i ≤ len - searchLen, in descending order, do
		// a. Let candidate be the substring of S from i to i + searchLen.
		// b. If candidate is the same sequence of code units as searchStr, return 𝔽(i).
		// 12. Return -1𝔽.
		return new NumberValue(S.lastIndexOf(searchStr, pos));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.localecompare")
	private static Value<?> localeCompare(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.localeCompare");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.match")
	private static Value<?> match(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.match");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.matchall")
	private static Value<?> matchAll(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.matchAll");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.normalize")
	private static Value<?> normalize(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.normalize");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padend")
	private static Value<?> padEnd(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.padEnd");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padstart")
	private static Value<?> padStart(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.padStart");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.repeat")
	private static Value<?> repeat(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.repeat");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.replace")
	private static Value<?> replace(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.replace");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.replaceall")
	private static Value<?> replaceAll(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.replaceAll");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.search")
	private static Value<?> search(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.search");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.split")
	private static Value<?> split(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("String.prototype.split");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.startswith")
	@NonCompliant
	private static Value<?> startsWith(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.23 String.prototype.startsWith ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.startsWith");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;

		// FIXME: 3. Let isRegExp be ? IsRegExp(searchString).
		//         4. If isRegExp is true, throw a TypeError exception.

		// 5. Let searchStr be ? ToString(searchString).
		final String searchStr = searchString.toStringValue(interpreter).value;
		// 6. Let len be the length of S.
		final int len = S.length();
		// 7. If position is undefined, let pos be 0; else let pos be ? ToIntegerOrInfinity(position).
		final int pos = position == Undefined.instance ? 0 : toIntegerOrInfinity(interpreter, position);
		// 8. Let start be the result of clamping pos between 0 and len.
		final int start = Math.max(0, Math.min(pos, len));
		// 9. Let searchLength be the length of searchStr.
		// 10. If searchLength = 0, return true.
		// 11. Let end be start + searchLength.
		// 12. If end > len, return false.
		// 13. Let substring be the substring of S from start to end.
		// 14. Return SameValueNonNumeric(substring, searchStr).
		return BooleanValue.of(S.startsWith(searchStr, start));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.substring")
	private static StringValue substring(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.24 String.prototype.substring ( start, end )
		final Value<?> start = argument(0, arguments);
		final Value<?> end = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.substring");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let len be the length of S.
		final int len = S.length();
		// 4. Let intStart be ? ToIntegerOrInfinity(start).
		final int intStart = toIntegerOrInfinity(interpreter, start);
		// 5. If end is undefined, let intEnd be len; else let intEnd be ? ToIntegerOrInfinity(end).
		final int intEnd = end == Undefined.instance ? len : toIntegerOrInfinity(interpreter, end);
		// 6. Let finalStart be the result of clamping intStart between 0 and len.
		final int finalStart = Math.max(0, Math.min(intStart, len));
		// 7. Let finalEnd be the result of clamping intEnd between 0 and len.
		final int finalEnd = Math.max(0, Math.min(intEnd, len));
		// 8. Let from be min(finalStart, finalEnd).
		final int from = Math.min(finalStart, finalEnd);
		// 9. Let `to` be max(finalStart, finalEnd).
		final int to = Math.max(finalStart, finalEnd);
		// 10. Return the substring of S from `from` to `to`.
		return new StringValue(S.substring(from, to));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tolocalelowercase")
	private static Value<?> toLocaleLowerCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return StringPrototype.toLowerCase(interpreter, arguments);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tolocaleuppercase")
	private static Value<?> toLocaleUpperCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		return StringPrototype.toUpperCase(interpreter, arguments);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tolowercase")
	private static Value<?> toLowerCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.toLowerCase");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let sText be ! StringToCodePoints(S).
		// 4. Let lowerText be the result of toLowercase(sText), according to the Unicode Default Case Conversion algorithm.
		// 5. Let L be ! CodePointsToString(lowerText).
		// 6. Return L.
		return new StringValue(S.value.toLowerCase());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.touppercase")
	private static Value<?> toUpperCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.toUpperCase");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let sText be ! StringToCodePoints(S).
		// 4. Let lowerText be the result of toLowercase(sText), according to the Unicode Default Case Conversion algorithm.
		// 5. Let L be ! CodePointsToString(lowerText).
		// 6. Return L.
		return new StringValue(S.value.toUpperCase());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.trimend")
	private static StringValue trimEnd(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter.thisValue(), "String.prototype.trimEnd");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// let T be the String value that is a copy of S with trailing white space removed.
		// 6. Return T.
		return new StringValue(S.value.stripTrailing());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.trimstart")
	private static StringValue trimStart(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter.thisValue(), "String.prototype.trimStart");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// let T be the String value that is a copy of S with leading white space removed.
		// 6. Return T.
		return new StringValue(S.value.stripLeading());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.trim")
	private static StringValue trim(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter.thisValue(), "String.prototype.trim");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// b. Let T be the String value that is a copy of S with both leading and trailing white space removed.
		// 6. Return T.
		return new StringValue(S.value.strip());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.charat")
	private static StringValue charAt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// String.prototype.charAt ( pos )
		final Value<?> pos = argument(0, arguments);
		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter.thisValue(), "String.prototype.charAt");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let position be ? ToIntegerOrInfinity(pos).
		final int position = toIntegerOrInfinity(interpreter, pos);
		// 4. Let size be the length of S.
		final int size = S.value.length();
		// 5. If position < 0 or position ≥ size, return the empty String.
		if (position < 0 || position >= size) return new StringValue("");
		// 6. Return the substring of S from position to position + 1.
		return new StringValue(S.value.substring(position, position + 1));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.slice")
	private static StringValue slice(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// String.prototype.slice ( start, end )
		final Value<?> start = argument(0, arguments);
		final Value<?> end = argument(1, arguments);
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
		if (from >= to) return new StringValue("");
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype-@@iterator")
	private static class StringIterator extends ObjectValue {
		private final PrimitiveIterator.OfInt primitiveIterator;

		@NonCompliant
		public StringIterator(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			final String value = thisStringValue(interpreter.thisValue()).value;
			this.primitiveIterator = value.codePoints().iterator();
			this.putMethod(Names.next, this::next);
		}

		private ObjectValue next(Interpreter interpreter, Value<?>[] arguments) {
			final ObjectValue result = new ObjectValue();
			if (!primitiveIterator.hasNext()) {
				result.put(Names.done, BooleanValue.TRUE);
				result.put(Names.value, Undefined.instance);
				return result;
			}

			result.put(Names.done, BooleanValue.FALSE);
			result.put(Names.value, new StringValue(new String(new int[] { primitiveIterator.nextInt() }, 0, 1)));
			return result;
		}
	}
}