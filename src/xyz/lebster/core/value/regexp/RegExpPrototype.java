package xyz.lebster.core.value.regexp;

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

	private static Value<?> split(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("RegExp.prototype[@@split]");
	}
}