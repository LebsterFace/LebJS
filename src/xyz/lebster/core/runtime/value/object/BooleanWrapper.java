package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.prototype.BooleanPrototype;

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