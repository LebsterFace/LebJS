package xyz.lebster.core.value.error.reference;

import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.error.ErrorPrototype;
import xyz.lebster.core.value.object.ObjectValue;

public class ReferenceErrorPrototype extends ErrorPrototype {
	public ReferenceErrorPrototype(Intrinsics intrinsics) {
		super(intrinsics);
	}
}
