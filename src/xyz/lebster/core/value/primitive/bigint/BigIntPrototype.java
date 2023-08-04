package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.object.ObjectValue;

public class BigIntPrototype extends ObjectValue {
	public BigIntPrototype(Intrinsics intrinsics) {
		super(intrinsics.bigIntPrototype);
	}
}
