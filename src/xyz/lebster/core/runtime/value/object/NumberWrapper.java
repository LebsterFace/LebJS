package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.prototype.NumberPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-objects")
public final class NumberWrapper extends PrimitiveWrapper<NumberValue, NumberPrototype> implements HasBuiltinTag {
	public NumberWrapper(NumberPrototype prototype, NumberValue data) {
		super(prototype, data);
	}

	@Override
	public String getBuiltinTag() {
		return "Number";
	}
}