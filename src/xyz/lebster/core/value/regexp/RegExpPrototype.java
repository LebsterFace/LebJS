package xyz.lebster.core.value.regexp;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.dotAll")
	private static Value<?> dotAll(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.dotAll");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.flags")
	private static Value<?> flags(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.flags");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.global")
	private static Value<?> global(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.global");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.ignorecase")
	private static Value<?> hasIndices(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.hasIndices");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.ignorecase")
	private static Value<?> ignoreCase(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.ignoreCase");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.multiline")
	private static Value<?> multiline(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.multiline");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.source")
	private static Value<?> source(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.source");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.sticky")
	private static Value<?> sticky(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.sticky");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.unicode")
	private static Value<?> unicode(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.unicode");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-get-regexp.prototype.unicodesets")
	private static Value<?> unicodeSets(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.unicodeSets");
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
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype.toString");
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isregexp")
	public static boolean isRegExp(Interpreter interpreter, Value<?> argument) {
		// 7.2.8 IsRegExp ( argument )

		// TODO: Symbol.match
		return argument instanceof RegExpObject;
	}
}