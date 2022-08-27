package xyz.lebster.core.value.primitive;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.function.NativeFunction;
import xyz.lebster.core.value.primitive.string.StringValue;

@NonStandard
public abstract class PrimitiveConstructor extends Executable {
	public PrimitiveConstructor(FunctionPrototype functionPrototype, StringValue name) {
		super(functionPrototype, name);
	}

	@Override
	public final StringValue toStringMethod() {
		return NativeFunction.toStringForName(this.name.value);
	}
}
