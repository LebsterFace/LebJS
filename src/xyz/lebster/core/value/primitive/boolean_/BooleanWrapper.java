package xyz.lebster.core.value.primitive.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean-objects")
public final class BooleanWrapper extends PrimitiveWrapper<BooleanValue, BooleanPrototype> implements HasBuiltinTag {
	public BooleanWrapper(BooleanPrototype prototype, BooleanValue data) {
		super(prototype, data);
	}

	@Override
	public String getBuiltinTag() {
		return "Boolean";
	}
}