package xyz.lebster.core.value.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-objects")
public final class SymbolWrapper extends PrimitiveWrapper<SymbolValue, SymbolPrototype> {
	public SymbolWrapper(SymbolPrototype prototype, SymbolValue data) {
		super(prototype, data);
	}
}