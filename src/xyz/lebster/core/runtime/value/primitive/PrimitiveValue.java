package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.runtime.value.Value;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value) {
		super(value);
	}
}