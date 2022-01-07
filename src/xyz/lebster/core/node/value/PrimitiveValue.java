package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.StringRepresentation;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value, Type type) {
		super(value, type);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value);
	}
}