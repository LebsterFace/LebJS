package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bigint-objects")
public class BigIntWrapper extends PrimitiveWrapper<BigIntValue, BigIntPrototype> {
	public BigIntWrapper(Intrinsics intrinsics, BigIntValue data) {
		super(intrinsics.bigIntPrototype, data);
	}
}
