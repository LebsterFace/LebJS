package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.StringRepresentation;

public abstract class Primitive<JType> extends Value<JType> {
	public Primitive(JType value, Type type) {
		super(value, type);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(toStringWithoutSideEffects());
	}
}