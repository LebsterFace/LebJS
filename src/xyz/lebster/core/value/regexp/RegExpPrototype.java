package xyz.lebster.core.value.regexp;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

public class RegExpPrototype extends ObjectValue {
	public RegExpPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, SymbolValue.split, 0, RegExpPrototype::split);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage/abstract-operations.html#sec-isregexp")
	@NonCompliant
	public static boolean isRegExp(Interpreter interpreter, Value<?> argument) {
		// 7.2.8 IsRegExp ( argument )

		// TODO: Symbol.match
		return argument instanceof RegExpObject;
	}

	private static Value<?> split(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype[@@split]");
	}
}