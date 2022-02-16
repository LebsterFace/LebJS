package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.constructor.SymbolConstructor;
import xyz.lebster.core.runtime.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-symbol-prototype-object")
public final class SymbolPrototype extends ObjectValue {
	public static final SymbolPrototype instance = new SymbolPrototype();

	static {
		instance.put(Names.constructor, SymbolConstructor.instance);
	}

	private SymbolPrototype() {
	}
}