package xyz.lebster.core.value.primitive;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.primitive.string.StringValue;

@NonStandard
public abstract class PrimitiveConstructor extends Executable {
	public PrimitiveConstructor(Intrinsics intrinsics, StringValue name) {
		super(intrinsics, name, 1); // Primitive constructors have .length = 1
	}

	@Override
	public final StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}
}
