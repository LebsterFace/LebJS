package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value, Type type) {
		super(value, type);
	}

	@Override
	public void display(StringRepresentation builder) {
		builder.append(value);
	}
}