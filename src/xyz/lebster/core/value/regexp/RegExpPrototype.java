package xyz.lebster.core.value.regexp;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

public class RegExpPrototype extends ObjectValue {
	public RegExpPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		this.putMethod(intrinsics.functionPrototype, SymbolValue.split, 0, (interpreter, arguments) -> {
			throw new NotImplemented("RegExp.prototype[@@split]");
		});
	}
}