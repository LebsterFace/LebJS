package xyz.lebster.core.value.regexp;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-regexp-prototype-object")
public class RegExpPrototype extends ObjectValue {
	public RegExpPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putAccessor(intrinsics, Names.dotAll, RegExpPrototype::dotAll, null, false, true);
		putAccessor(intrinsics, Names.flags, RegExpPrototype::flags, null, false, true);
		putAccessor(intrinsics, Names.global, RegExpPrototype::global, null, false, true);
		putAccessor(intrinsics, Names.hasIndices, RegExpPrototype::hasIndices, null, false, true);
		putAccessor(intrinsics, Names.ignoreCase, RegExpPrototype::ignoreCase, null, false, true);
		putAccessor(intrinsics, Names.multiline, RegExpPrototype::multiline, null, false, true);
		putAccessor(intrinsics, Names.source, RegExpPrototype::source, null, false, true);
		putAccessor(intrinsics, Names.sticky, RegExpPrototype::sticky, null, false, true);
		putAccessor(intrinsics, Names.unicode, RegExpPrototype::unicode, null, false, true);
		putAccessor(intrinsics, Names.unicodeSets, RegExpPrototype::unicodeSets, null, false, true);
		putMethod(intrinsics, Names.exec, 1, RegExpPrototype::exec);
		putMethod(intrinsics, Names.test, 1, RegExpPrototype::test);
		putMethod(intrinsics, Names.toString, 0, RegExpPrototype::toStringMethod);
		putMethod(intrinsics, SymbolValue.match, 1, RegExpPrototype::match);
		putMethod(intrinsics, SymbolValue.matchAll, 1, RegExpPrototype::matchAll);
		putMethod(intrinsics, SymbolValue.replace, 2, RegExpPrototype::replace);
		putMethod(intrinsics, SymbolValue.search, 1, RegExpPrototype::search);
		putMethod(intrinsics, SymbolValue.split, 2, RegExpPrototype::split);
	}

	@NonStandard
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.flags")
	private static Value<?> flags(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. If R is not an Object, throw a TypeError exception.
		if (!(R instanceof final RegExpObject re))
			throw error(new TypeError(interpreter, "RegExp.prototype.flags requires that 'this' be an object."));
		// 3. Let codeUnits be a new empty List.
		final StringBuilder codeUnits = new StringBuilder();
		// 4. Let hasIndices be ToBoolean(? Get(R, "hasIndices")).
		final boolean hasIndices = re.get(interpreter, Names.hasIndices).isTruthy(interpreter);
		// 5. If hasIndices is true, append the code unit 0x0064 (LATIN SMALL LETTER D) to codeUnits.
		if (hasIndices) codeUnits.append('d');
		// 6. Let global be ToBoolean(? Get(R, "global")).
		final boolean global = re.get(interpreter, Names.global).isTruthy(interpreter);
		// 7. If global is true, append the code unit 0x0067 (LATIN SMALL LETTER G) to codeUnits.
		if (global) codeUnits.append('g');
		// 8. Let ignoreCase be ToBoolean(? Get(R, "ignoreCase")).
		final boolean ignoreCase = re.get(interpreter, Names.ignoreCase).isTruthy(interpreter);
		// 9. If ignoreCase is true, append the code unit 0x0069 (LATIN SMALL LETTER I) to codeUnits.
		if (ignoreCase) codeUnits.append('i');
		// 10. Let multiline be ToBoolean(? Get(R, "multiline")).
		final boolean multiline = re.get(interpreter, Names.multiline).isTruthy(interpreter);
		// 11. If multiline is true, append the code unit 0x006D (LATIN SMALL LETTER M) to codeUnits.
		if (multiline) codeUnits.append('m');
		// 12. Let dotAll be ToBoolean(? Get(R, "dotAll")).
		final boolean dotAll = re.get(interpreter, Names.dotAll).isTruthy(interpreter);
		// 13. If dotAll is true, append the code unit 0x0073 (LATIN SMALL LETTER S) to codeUnits.
		if (dotAll) codeUnits.append('s');
		// 14. Let unicode be ToBoolean(? Get(R, "unicode")).
		final boolean unicode = re.get(interpreter, Names.unicode).isTruthy(interpreter);
		// 15. If unicode is true, append the code unit 0x0075 (LATIN SMALL LETTER U) to codeUnits.
		if (unicode) codeUnits.append('u');
		// 16. Let unicodeSets be ToBoolean(? Get(R, "unicodeSets")).
		final boolean unicodeSets = re.get(interpreter, Names.unicodeSets).isTruthy(interpreter);
		// 17. If unicodeSets is true, append the code unit 0x0076 (LATIN SMALL LETTER V) to codeUnits.
		if (unicodeSets) codeUnits.append('v');
		// 18. Let sticky be ToBoolean(? Get(R, "sticky")).
		final boolean sticky = re.get(interpreter, Names.sticky).isTruthy(interpreter);
		// 19. If sticky is true, append the code unit 0x0079 (LATIN SMALL LETTER Y) to codeUnits.
		if (sticky) codeUnits.append('y');
		// 20. Return the String value whose code units are the elements of the List codeUnits.
		// If codeUnits has no elements, the empty String is returned.
		return new StringValue(codeUnits.toString());
	}

	@NonStandard
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexphasflag")
	private static BooleanValue regExpHasFlag(Interpreter interpreter, String methodName, Value<?> R, char codeUnit) throws AbruptCompletion {
		// 1. If R is not an Object, throw a TypeError exception.
		// 2. If R does not have an [[OriginalFlags]] internal slot, then
		//     a. If SameValue(R, %RegExp.prototype%) is true, return undefined.
		//     b. Otherwise, throw a TypeError exception.
		if (!(R instanceof final RegExpObject re))
			throw error(new TypeError(interpreter, "RegExp.prototype.%s requires that 'this' be a RegExp.".formatted(methodName)));
		// 3. Let flags be R.[[OriginalFlags]].
		final String flags = re.flags;
		// 4. If flags contains codeUnit, return true.
		// 5. Return false.
		return BooleanValue.of(flags.indexOf(codeUnit) != -1);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.dotAll")
	private static BooleanValue dotAll(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0073 (LATIN SMALL LETTER S).
		final char cu = 's';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "dotAll", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.global")
	private static BooleanValue global(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0067 (LATIN SMALL LETTER G).
		final char cu = 'g';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "global", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.ignorecase")
	private static BooleanValue hasIndices(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0064 (LATIN SMALL LETTER D).
		final char cu = 'd';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "hasIndices", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.ignorecase")
	private static Value<?> ignoreCase(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0069 (LATIN SMALL LETTER I).
		final char cu = 'i';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "ignoreCase", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.multiline")
	private static Value<?> multiline(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x006D (LATIN SMALL LETTER M).
		final char cu = 'm';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "multiline", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.sticky")
	private static BooleanValue sticky(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0079 (LATIN SMALL LETTER Y).
		final char cu = 'y';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "sticky", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.unicode")
	private static BooleanValue unicode(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0075 (LATIN SMALL LETTER U).
		final char cu = 'u';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "unicode", R, cu);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.unicodesets")
	private static BooleanValue unicodeSets(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. Let cu be the code unit 0x0076 (LATIN SMALL LETTER V).
		final char cu = 'v';
		// 3. Return ? RegExpHasFlag(R, cu).
		return regExpHasFlag(interpreter, "unicodeSets", R, cu);
	}

	@NonStandard
	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.source")
	private static Value<?> source(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. If R is not an Object, throw a TypeError exception.
		if (!(R instanceof final RegExpObject re))
			throw error(new TypeError(interpreter, "RegExp.prototype.source requires that 'this' be a RegExp."));
		// 4. Assert: R has an [[OriginalFlags]] internal slot.
		// 5. Let src be R.[[OriginalSource]].
		final String src = re.source;
		// 6. Let flags be R.[[OriginalFlags]].
		// TODO: 7. Return EscapeRegExpPattern(src, flags).
		return new StringValue(src);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype-@@match")
	private static Value<?> match(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype [ @@match ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp-prototype-matchall")
	private static Value<?> matchAll(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype [ @@matchAll ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype-@@replace")
	private static Value<?> replace(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype [ @@replace ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype-@@search")
	private static Value<?> search(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype [ @@search ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype-@@split")
	private static ArrayObject split(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype [ @@split ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype.exec")
	private static Value<?> exec(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.exec");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype.test")
	private static BooleanValue test(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.test");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-regexp.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 22.2.6.17 RegExp.prototype.toString ( )

		// 1. Let R be the `this` value.
		final Value<?> R = interpreter.thisValue();
		// 2. If R is not an Object, throw a TypeError exception.
		if (!(R instanceof final ObjectValue object))
			throw error(new TypeError(interpreter, "RegExp.prototype.toString requires that 'this' be an object."));
		// 3. Let pattern be ? ToString(? Get(R, "source")).
		final String pattern = object.get(interpreter, Names.source).toStringValue(interpreter).value;
		// 4. Let flags be ? ToString(? Get(R, "flags")).
		final String flags = object.get(interpreter, Names.flags).toStringValue(interpreter).value;
		// 5. Let result be the string-concatenation of "/", pattern, "/", and flags.
		final String result = "/" + pattern + "/" + flags;
		// 6. Return result.
		return new StringValue(result);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isregexp")
	public static boolean isRegExp(Interpreter interpreter, Value<?> argument) {
		// 7.2.8 IsRegExp ( argument )

		// TODO: Symbol.match
		return argument instanceof RegExpObject;
	}
}