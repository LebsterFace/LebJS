package xyz.lebster.core.runtime.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.prototype.NumberPrototype;

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