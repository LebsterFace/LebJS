package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-objects")
public final class NumberWrapper extends PrimitiveWrapper<NumberValue, NumberPrototype> implements HasBuiltinTag {
	public NumberWrapper(Intrinsics intrinsics, NumberValue data) {
		super(intrinsics.numberPrototype, data);
	}

	@Override
	public String getBuiltinTag() {
		return "Number";
	}
}