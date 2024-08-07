package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.iterator.IteratorObject;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;
import xyz.lebster.core.value.regexp.RegExpObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.object.ObjectPrototype.requireObjectCoercible;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toLength;
import static xyz.lebster.core.value.primitive.number.NumberValue.UINT32_LIMIT;
import static xyz.lebster.core.value.regexp.RegExpPrototype.isRegExp;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-string-prototype-object")
public final class StringPrototype extends ObjectValue {

	public StringPrototype(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, Names.at, 1, StringPrototype::at);
		putMethod(intrinsics, Names.charAt, 1, StringPrototype::charAt);
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
		putMethod(intrinsics, Names.slice, 2, StringPrototype::slice);
		putMethod(intrinsics, Names.split, 2, StringPrototype::split);
		putMethod(intrinsics, Names.startsWith, 1, StringPrototype::startsWith);
		putMethod(intrinsics, Names.substring, 2, StringPrototype::substring);
		putMethod(intrinsics, Names.toLocaleLowerCase, 0, StringPrototype::toLocaleLowerCase);
		putMethod(intrinsics, Names.toLocaleUpperCase, 0, StringPrototype::toLocaleUpperCase);
		putMethod(intrinsics, Names.toLowerCase, 0, StringPrototype::toLowerCase);
		putMethod(intrinsics, Names.toString, 0, StringPrototype::toStringMethod);
		putMethod(intrinsics, Names.toUpperCase, 0, StringPrototype::toUpperCase);
		putMethod(intrinsics, Names.trim, 0, StringPrototype::trim);
		putMethod(intrinsics, Names.trimEnd, 0, StringPrototype::trimEnd);
		putMethod(intrinsics, Names.trimStart, 0, StringPrototype::trimStart);
		putMethod(intrinsics, Names.valueOf, 0, StringPrototype::valueOf);
		putMethod(intrinsics, SymbolValue.iterator, 0, StringIterator::new);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.at")
	private static Value<?> at(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.1 String.prototype.at ( index )
		final Value<?> index = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.at");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;
		// 3. Let len be the length of S.
		final int len = S.length();
		// 4. Let relativeIndex be ? ToIntegerOrInfinity(index).
		final int relativeIndex = toIntegerOrInfinity(interpreter, index);
		// If relativeIndex ≥ 0, then Let k be relativeIndex. Else, Let k be len + relativeIndex.
		final int k = relativeIndex >= 0 ? relativeIndex : len + relativeIndex;
		// 7. If k < 0 or k ≥ len, return undefined.
		if (k < 0 || k >= len) return Undefined.instance;
		// 8. Return the substring of S from k to k + 1.
		return new StringValue(S.substring(k, k + 1));
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

		// 3. Let isRegExp be ? IsRegExp(searchString).
		final boolean isRegExp = isRegExp(interpreter, searchString);
		// 4. If isRegExp is true, throw a TypeError exception.
		if (isRegExp)
			throw error(new TypeError(interpreter, "First argument to String.prototype.endsWith must not be a regular expression"));

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
	private static BooleanValue includes(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.8 String.prototype.includes ( searchString [ , position ] )
		final Value<?> searchString = argument(0, arguments);
		final Value<?> position = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.includes");
		// 2. Let S be ? ToString(O).
		final String S = O.toStringValue(interpreter).value;

		// 3. Let isRegExp be ? IsRegExp(searchString).
		final boolean isRegExp = isRegExp(interpreter, searchString);
		// 4. If isRegExp is true, throw a TypeError exception.
		if (isRegExp)
			throw error(new TypeError(interpreter, "First argument to String.prototype.includes must not be a regular expression"));

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

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.localecompare")
	private static Value<?> localeCompare(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.11 String.prototype.localeCompare ( that [ , reserved1 [ , reserved2 ] ] )
		final Value<?> that = argument(0, arguments);
		final Value<?> reserved1 = argument(1, arguments);
		final Value<?> reserved2 = argument(2, arguments);

		throw new NotImplemented("String.prototype.localeCompare");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.match")
	private static Value<?> match(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.12 String.prototype.match ( regexp )
		final Value<?> regexp = argument(0, arguments);

		if (!(regexp instanceof final RegExpObject re)) {
			throw new NotImplemented("String.prototype.match for non RegExp objects");
		}

		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.match");
		final StringValue S = O.toStringValue(interpreter);

		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/match#return_value
		final Matcher matcher = re.pattern.matcher(S.value);
		if (!matcher.find()) return Null.instance;

		if (re.isGlobal()) {
			// If the g flag is used, all results matching the complete regular expression
			// will be returned, but capturing groups are not included.
			final ArrayList<StringValue> results = new ArrayList<>();
			do {
				results.add(new StringValue(matcher.group()));
			} while (matcher.find());

			return new ArrayObject(interpreter, results);
		} else {
			final StringValue[] elements = new StringValue[matcher.groupCount() + 1];
			// The returned array has the matched text as the first item, and then
			// one item for each capturing group of the matched text.
			for (int i = 0; i < matcher.groupCount() + 1; i++) {
				elements[i] = new StringValue(matcher.group(i));
			}

			final ArrayObject result = new ArrayObject(interpreter, elements);

			// The array also has the following additional properties:
			// index - The 0-based index of the match in the string.
			result.set(interpreter, Names.index, new NumberValue(matcher.start()));
			// input - The original string that was matched against.
			result.set(interpreter, Names.input, S);
			// groups - A null-prototype object of named capturing groups,
			// whose keys are the names, and values are the capturing groups,
			// or undefined if no named capturing groups were defined.
			if (matcher.namedGroups().size() == 0) {
				result.set(interpreter, Names.groups, Undefined.instance);
			} else {
				final ObjectValue groups = new ObjectValue(Null.instance);
				for (final var entry : matcher.namedGroups().entrySet()) {
					final StringValue key = new StringValue(entry.getKey());
					final StringValue value = new StringValue(matcher.group(entry.getValue()));
					groups.put(key, value);
				}

				result.set(interpreter, Names.groups, groups);
			}

			// TODO: other properties
			return result;
		}
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.matchall")
	private static Value<?> matchAll(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.13 String.prototype.matchAll ( regexp )
		final Value<?> regexp = argument(0, arguments);

		throw new NotImplemented("String.prototype.matchAll");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.normalize")
	private static StringValue normalize(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.3.14 String.prototype.normalize ( [ form ] )
		final Value<?> form = argument(0, arguments);

		throw new NotImplemented("String.prototype.normalize");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padend")
	private static StringValue padEnd(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.16 String.prototype.padEnd ( maxLength [ , fillString ] )
		final Value<?> maxLength = argument(0, arguments);
		final Value<?> fillString = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.padEnd");
		// 2. Return ? StringPaddingBuiltinsImpl(O, maxLength, fillString, end).
		return stringPaddingBuiltinsImpl(interpreter, O, maxLength, fillString, false);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.padstart")
	private static StringValue padStart(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.17 String.prototype.padStart ( maxLength [ , fillString ] )
		final Value<?> maxLength = argument(0, arguments);
		final Value<?> fillString = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.padStart");
		// 2. Return ? StringPaddingBuiltinsImpl(O, maxLength, fillString, start).
		return stringPaddingBuiltinsImpl(interpreter, O, maxLength, fillString, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-stringpaddingbuiltinsimpl")
	private static StringValue stringPaddingBuiltinsImpl(Interpreter interpreter, Value<?> O, Value<?> maxLength, Value<?> fillString_, boolean start) throws AbruptCompletion {
		// 1. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 2. Let intMaxLength be ℝ(? ToLength(maxLength)).
		final int intMaxLength = toLength(interpreter, maxLength);
		// 3. Let stringLength be the length of S.
		final int stringLength = S.value.length();
		// 4. If intMaxLength ≤ stringLength, return S.
		if (intMaxLength <= stringLength) return S;
		// 5. If fillString is undefined, set fillString to the String value consisting solely of the code unit 0x0020 (SPACE).
		// 6. Else, set fillString to ? ToString(fillString).
		final String fillString = fillString_ == Undefined.instance ? " " : fillString_.toStringValue(interpreter).value;
		// 7. Return StringPad(S, intMaxLength, fillString, placement).
		return new StringValue(stringPad(S.value, intMaxLength, fillString, start));
	}

	public static String stringPad(String S, int maxLength, String fillString, boolean start) {
		// 1. Let stringLength be the length of S.
		final int stringLength = S.length();
		// 2. If maxLength ≤ stringLength, return S.
		if (maxLength <= stringLength) return S;
		// 3. If fillString is the empty String, return S.
		if (fillString.isEmpty()) return S;
		// 4. Let fillLen be maxLength - stringLength.
		final int fillLen = maxLength - stringLength;
		// 5. Let truncatedStringFiller be the String value consisting of repeated concatenations of fillString truncated to length fillLen.
		final String truncatedStringFiller = fillString.repeat((fillLen / fillString.length()) + 1).substring(0, fillLen);
		// 6. If placement is start, return the string-concatenation of truncatedStringFiller and S.
		if (start) return truncatedStringFiller + S;
		// 7. Else, return the string-concatenation of S and truncatedStringFiller.
		else return S + truncatedStringFiller;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.repeat")
	private static StringValue repeat(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.17 String.prototype.repeat ( count )
		final Value<?> count = argument(0, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.repeat");
		// 2. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 3. Let n be ? ToIntegerOrInfinity(count).
		final int n = toIntegerOrInfinity(interpreter, count);
		// 4. If n < 0 or n is +∞, throw a RangeError exception.
		if (n < 0 || n == Integer.MAX_VALUE)
			throw error(new RangeError(interpreter, "Invalid count value: %s".formatted(count.toDisplayString(true))));
		// 5. If n is 0, return the empty String.
		if (n == 0) return StringValue.EMPTY;
		// 6. Return the String value that is made from n copies of S appended together.
		return new StringValue(S.value.repeat(n));
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
	private static Value<?> split(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.22 String.prototype.split ( separator, limit )
		final Value<?> separator = argument(0, arguments);
		final Value<?> limit = argument(1, arguments);

		// 1. Let O be ? RequireObjectCoercible(this value).
		final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype.split");
		// 2. If separator is neither undefined nor null, then

		if (!separator.isNullish()) {
			// a. Let splitter be ? GetMethod(separator, @@split).
			final var splitter = separator.toObjectValue(interpreter).getMethod(interpreter, SymbolValue.split);
			// b. If splitter is not undefined, then
			if (splitter != null)
				// i. Return ? Call(splitter, separator, « O, limit »).
				return splitter.call(interpreter, separator, O, limit);
		}

		// 3. Let S be ? ToString(O).
		final StringValue S = O.toStringValue(interpreter);
		// 4. If limit is undefined, let lim be 2^32 - 1; else let lim be ℝ(? ToUint32(limit)).
		final long lim = limit == Undefined.instance ? UINT32_LIMIT : limit.toNumberValue(interpreter).toUint32();
		// 5. Let R be ? ToString(separator).
		final StringValue R = separator.toStringValue(interpreter);
		// 6. If lim = 0, then
		if (lim == 0) {
			// a. Return CreateArrayFromList(« »).
			return new ArrayObject(interpreter, 0);
		}
		// 7. If separator is undefined, then
		if (separator == Undefined.instance) {
			// a. Return CreateArrayFromList(« S »).
			return new ArrayObject(interpreter, S);
		}
		// 8. Let separatorLength be the length of R.
		final int separatorLength = R.value.length();
		// 9. If separatorLength is 0, then
		if (separatorLength == 0) {
			// a. Let head be the substring of S from 0 to lim.
			final String head = S.value.substring(0, Math.toIntExact(Math.min(lim, S.value.length())));
			// b. Let codeUnits be a List consisting of the sequence of code units that are the elements of head.
			final byte[] codeUnits_bytes = head.getBytes(StandardCharsets.UTF_8);
			final ArrayList<StringValue> codeUnits = new ArrayList<>();
			for (final byte b : codeUnits_bytes) {
				codeUnits.add(new StringValue((char) b));
			}

			// c. Return CreateArrayFromList(codeUnits).
			return new ArrayObject(interpreter, codeUnits);
		}

		// 10. If S is the empty String, return CreateArrayFromList(« S »).
		if (S.value.isEmpty()) return new ArrayObject(interpreter, S);

		// 11. Let substrings be a new empty List.
		final var substrings = new ArrayList<StringValue>();
		// 12. Let i be 0.
		int i = 0;
		// 13. Let j be StringIndexOf(S, R, 0).
		int j = S.value.indexOf(R.value);
		// 14. Repeat, while j is not -1,
		while (j != -1) {
			// a. Let T be the substring of S from i to j.
			final String T = S.value.substring(i, j);
			// b. Append T to substrings.
			substrings.add(new StringValue(T));
			// c. If the number of elements of substrings is lim, return CreateArrayFromList(substrings).
			if (substrings.size() == lim) return new ArrayObject(interpreter, substrings);
			// d. Set i to j + separatorLength.
			i = j + separatorLength;
			// e. Set j to StringIndexOf(S, R, i).
			j = S.value.indexOf(R.value, i);
		}

		// 15. Let T be the substring of S from `i`.
		final String T = S.value.substring(i);
		// 16. Append T to substrings.
		substrings.add(new StringValue(T));
		// 17. Return CreateArrayFromList(substrings).
		return new ArrayObject(interpreter, substrings);
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

		// 3. Let isRegExp be ? IsRegExp(searchString).
		final boolean isRegExp = isRegExp(interpreter, searchString);
		// 4. If isRegExp is true, throw a TypeError exception.
		if (isRegExp)
			throw error(new TypeError(interpreter, "First argument to String.prototype.startsWith must not be a regular expression"));

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
		return thisStringValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.1.3.28 String.prototype.toString ( )

		// 1. Return ? thisStringValue(this value).
		return thisStringValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#thisstringvalue")
	private static StringValue thisStringValue(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();
		// 1. If value is a String, return value.
		if (value instanceof StringValue stringValue) return stringValue;
		// 2. If value is an Object and value has a [[StringData]] internal slot, return value.[[StringData]].
		if (value instanceof StringWrapper stringWrapper) return stringWrapper.data;
		// 3. Throw a TypeError exception.
		throw error(interpreter.incompatibleReceiver("String.prototype", "a String"));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.prototype-@@iterator")
	private static class StringIterator extends IteratorObject {
		private final PrimitiveIterator.OfInt primitiveIterator;

		@NonCompliant
		public StringIterator(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
			super(interpreter.intrinsics);
			// 1. Let O be ? RequireObjectCoercible(this value).
			final Value<?> O = requireObjectCoercible(interpreter, interpreter.thisValue(), "String.prototype[Symbol.iterator]");
			// 2. Let S be ? ToString(O).
			final String S = O.toStringValue(interpreter).value;
			this.primitiveIterator = S.codePoints().iterator();
			if (!primitiveIterator.hasNext()) setCompleted();
		}

		@Override
		public Value<?> next(Interpreter interpreter, Value<?>[] arguments) {
			if (!primitiveIterator.hasNext()) return setCompleted();

			return new StringValue(new String(new int[] { primitiveIterator.nextInt() }, 0, 1));
		}
	}
}