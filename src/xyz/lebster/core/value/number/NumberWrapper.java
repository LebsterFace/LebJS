package xyz.lebster.core.value.number;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.PrimitiveWrapper;

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