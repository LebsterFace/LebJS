package xyz.lebster.core.value.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class BooleanWrapper extends PrimitiveWrapper<BooleanValue, BooleanPrototype> implements HasBuiltinTag {
	public BooleanWrapper(BooleanPrototype prototype, BooleanValue data) {
		super(prototype, data);
	}

	@Override
	public String getBuiltinTag() {
		return "Boolean";
	}
}