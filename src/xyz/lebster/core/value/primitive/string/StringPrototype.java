package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Generator;
import xyz.lebster.core.value.IteratorResult;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.PrimitiveIterator;
import java.util.regex.Pattern;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.object.ObjectPrototype.requireObjectCoercible;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-string-prototype-object")
public final class StringPrototype extends ObjectValue {

	public StringPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.slice, 2, StringPrototype::slice);
		putMethod(intrinsics, Names.charAt, 1, StringPrototype::charAt);
		putMethod(intrinsics, Names.trim, 0, StringPrototype::trim);
		putMethod(intrinsics, Names.trimStart, 0, StringPrototype::trimStart);
		putMethod(intrinsics, Names.trimEnd, 0, StringPrototype::trimEnd);
		putMethod(intrinsics, Names.toUpperCase, 0, StringPrototype::toUpperCase);
		putMethod(intrinsics, Names.toLowerCase, 0, StringPrototype::toLowerCase);
		putMethod(intrinsics, Names.valueOf, 0, StringPrototype::valueOf);
		putMethod(intrinsics, Names.toString, 0, StringPrototype::toStringMethod);
		putMethod(intrinsics, SymbolValue.iterator, 0, StringIterator::new);
		putMethod(intrinsics, Names.charCodeAt, 1, StringPrototype::charCodeAt);
		putMethod(intrinsics, Names.codePointAt, 1, StringPrototype::codePointAt);
		putMethod(intrinsics, Names.concat, 0, StringPrototype::concat);
		putMethod(intrinsics, Names.endsWith, 1, StringPrototype::endsWith);
		putMethod(intrinsics, Names.includes, 1, StringPrototype::includes);
		putMethod(intrinsics, Names.indexOf, 1, StringPrototype::indexOf);
		putMethod(intrinsics, Names.lastIndexOf, 1, StringPrototype::lastIndexOf);
		putMethod(intrinsics, Names.localeCompare, 1, StringPrototype::localeCompare);
		putMethod(intrinsics, Names.match, 1, StringPrototype::match);
		putMethod(intrinsics, Names.matchAll, 1, StringPrototype::matchAll);
		putMethod(intrinsics, Names.normalize, 0, StringPrototype::normalize);
		putMethod(intrinsics, Names.padEnd, 1, StringPrototype::padEnd);
		putMethod(intrinsics, Names.padStart, 1, StringPrototype::padStart);
		putMethod(intrinsics, Names.repeat, 1, StringPrototype::repeat);
		putMethod(intrinsics, Names.replace, 2, StringPrototype::replace);
		putMethod(intrinsics, Names.replaceAll, 2, StringPrototype::replaceAll);
		putMethod(intrinsics, Names.search, 1, StringPrototype::search);
		putMethod(intrinsics, Names.split, 2, StringPrototype::split);
		putMethod(intrinsics, Names.startsWith, 1, StringPrototype::startsWith);
		putMethod(intrinsics, Names.substring, 2, StringPrototype::substring);
		putMethod(intrinsics, Names.toLocaleLowerCase, 0, StringPrototype::toLocaleLowerCase);
		putMethod(intrinsics, Names.toLocaleUpperCase, 0, StringPrototype::toLocaleUpperCase);

		// Non-standard
		putMethod(intrinsics, Names.reverse, 0, StringPrototype::reverse);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.charcodeat")
	private static NumberValue charCodeAt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.3 String.prototype.charCodeAt ( pos )
		final Value<?> pos = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.charCodeAt");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.codePointAt");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.concat");
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
	private static BooleanValue endsWith(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.7 String.prototype.endsWith ( searchString [ , endPosition ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> endPosition = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.endsWith");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.includes");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.indexOf");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.lastIndexOf");
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
		// 22.1.3.11 String.prototype.localeCompare ( that [ , reserved1 [ , reserved2 ] ] )
		final Value<?> that = argument(0, arguments);
		final Value<?> reserved1 = argument(1, arguments);
		final Value<?> reserved2 = argument(2, arguments);

		throw new NotImplemented("String.prototype.localeCompare");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.match")
	private static Value<?> match(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.12 String.prototype.match ( regexp )
		final Value<?> regexp = argument(0, arguments);

		throw new NotImplemented("String.prototype.match");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.matchall")
	private static Value<?> matchAll(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.13 String.prototype.matchAll ( regexp )
		final Value<?> regexp = argument(0, arguments);

		throw new NotImplemented("String.prototype.matchAll");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.normalize")
	private static StringValue normalize(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.14 String.prototype.normalize ( [ form ] )
		final Value<?> form = argument(0, arguments);

		throw new NotImplemented("String.prototype.normalize");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padend")
	private static StringValue padEnd(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.15 String.prototype.padEnd ( maxLength [ , fillString ] )
		final Value<?> maxLength = argument(0, arguments);
		final Value<?> fillString = argument(1, arguments);

		throw new NotImplemented("String.prototype.padEnd");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padstart")
	private static StringValue padStart(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.16 String.prototype.padStart ( maxLength [ , fillString ] )
		final Value<?> maxLength = argument(0, arguments);
		final Value<?> fillString = argument(1, arguments);

		throw new NotImplemented("String.prototype.padStart");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.repeat")
	private static StringValue repeat(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.17 String.prototype.repeat ( count )
		final Value<?> count = argument(0, arguments);

		throw new NotImplemented("String.prototype.repeat");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.replace")
	private static Value<?> replace(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.18 String.prototype.replace ( searchValue, replaceValue )
		final Value<?> searchValue = argument(0, arguments);
		final Value<?> replaceValue = argument(1, arguments);

		throw new NotImplemented("String.prototype.replace");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.replaceall")
	@NonCompliant
	// TODO: Check if this is spec-compliant (for strings)
	// TODO: RegExp & Functional replace
	private static Value<?> replaceAll(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.19 String.prototype.replaceAll ( searchValue, replaceValue )
		final Value<?> searchValue = argument(0, arguments);
		final Value<?> replaceValue = argument(1, arguments);

		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.replaceAll");
		final String string = O.toStringValue(interpreter).value;
		final String searchString = searchValue.toStringValue(interpreter).value;
		final String replaceString = replaceValue.toStringValue(interpreter).value;

		return new StringValue(string.replaceAll(Pattern.quote(searchString), replaceString));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.search")
	private static Value<?> search(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.20 String.prototype.search ( regexp )
		final Value<?> regexp = argument(0, arguments);

		throw new NotImplemented("String.prototype.search");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.split")
	private static Value<?> split(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.22 String.prototype.split ( separator, limit )
		final Value<?> separator = argument(0, arguments);
		final Value<?> limit = argument(1, arguments);

		throw new NotImplemented("String.prototype.split");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.startswith")
	@NonCompliant
	private static BooleanValue startsWith(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.23 String.prototype.startsWith ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.startsWith");
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
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.substring");
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
	private static Value<?> toLocaleLowerCase(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.25 String.prototype.toLocaleLowerCase ( [ reserved1 [ , reserved2 ] ] )

		throw new NotImplemented("String.prototype.toLocaleLowerCase");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tolocaleuppercase")
	private static Value<?> toLocaleUpperCase(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.26 String.prototype.toLocaleUpperCase ( [ reserved1 [ , reserved2 ] ] )

		throw new NotImplemented("String.prototype.toLocaleUpperCase");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tolowercase")
	private static Value<?> toLowerCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.27 String.prototype.toLowerCase ( )

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.toLowerCase");
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
		// 22.1.3.29 String.prototype.toUpperCase ( )

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.toUpperCase");
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
		// 22.1.3.31 String.prototype.trimEnd ( )

		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.trimEnd");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// let T be the String value that is a copy of S with trailing white space removed.
		// 6. Return T.
		return new StringValue(S.value.stripTrailing());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.trimstart")
	private static StringValue trimStart(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.32 String.prototype.trimStart ( )

		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.trimStart");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// let T be the String value that is a copy of S with leading white space removed.
		// 6. Return T.
		return new StringValue(S.value.stripLeading());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.trim")
	private static StringValue trim(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.30 String.prototype.trim ( )

		// 1. Let str be ? RequireObjectCoercible(string).
		final Value<?> str = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.trim");
		// 2. Let S be ? ToString(str).
		final StringValue S = str.toStringValue(interpreter);
		// b. Let T be the String value that is a copy of S with both leading and trailing white space removed.
		// 6. Return T.
		return new StringValue(S.value.strip());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.charat")
	private static StringValue charAt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.2 String.prototype.charAt ( pos )
		final Value<?> pos = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.charAt");
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
		// 22.1.3.21 String.prototype.slice ( start, end )
		final Value<?> start = argument(0, arguments);
		final Value<?> end = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.slice");
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
		// 22.1.3.33 String.prototype.valueOf ( )

		// 1. Return ? thisStringValue(this value).
		return thisStringValue(interpreter, interpreter.thisValue());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.28 String.prototype.toString ( )

		// 1. Return ? thisStringValue(this value).
		return thisStringValue(interpreter, interpreter.thisValue());
	}

	@NonStandard
	private static StringValue reverse(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// String.prototype.reverse(): string

		final String S = thisStringValue(interpreter, interpreter.thisValue()).value;
		return new StringValue(new StringBuilder(S).reverse().toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#thisstringvalue")
	private static StringValue thisStringValue(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
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
		throw error(new TypeError(interpreter, "This method requires that 'this' be a String"));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype-@@iterator")
	private static class StringIterator extends Generator {
		private final PrimitiveIterator.OfInt primitiveIterator;

		@NonCompliant
		public StringIterator(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			super(interpreter.intrinsics);
			final String value = thisStringValue(interpreter, interpreter.thisValue()).value;
			this.primitiveIterator = value.codePoints().iterator();
		}

		@Override
		public IteratorResult nextMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			if (!primitiveIterator.hasNext()) {
				return new IteratorResult(Undefined.instance, true);
			}

			return new IteratorResult(
				new StringValue(new String(new int[] { primitiveIterator.nextInt() }, 0, 1)),
				false
			);
		}
	}
}