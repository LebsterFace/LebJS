package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.prototype.SymbolPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol-objects")
public final class SymbolWrapper extends PrimitiveWrapper<SymbolValue, SymbolPrototype> {
	public SymbolWrapper(SymbolPrototype prototype, SymbolValue data) {
		super(prototype, data);
	}
}