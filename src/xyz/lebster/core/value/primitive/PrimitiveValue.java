package xyz.lebster.core.value.primitive;

import xyz.lebster.core.value.Value;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value) {
		super(value);
	}
}