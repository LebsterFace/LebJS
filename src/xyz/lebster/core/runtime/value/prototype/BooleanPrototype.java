package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-boolean-prototype-object")
public final class BooleanPrototype extends ObjectValue {
	public static final BooleanPrototype instance = new BooleanPrototype();

	static {
	}

	private BooleanPrototype() {
	}
}