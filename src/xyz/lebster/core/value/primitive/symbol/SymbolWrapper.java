package xyz.lebster.core.value.primitive.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-objects")
public final class SymbolWrapper extends PrimitiveWrapper<SymbolValue, SymbolPrototype> {
	public SymbolWrapper(Intrinsics intrinsics, SymbolValue data) {
		super(intrinsics.symbolPrototype, data);
	}
}