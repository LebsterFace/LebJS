package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.prototype.NumberPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-objects")
public final class NumberWrapper extends PrimitiveWrapper<NumberValue> {
	public NumberWrapper(NumberValue s) {
		super(s);
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return NumberPrototype.instance;
	}
}