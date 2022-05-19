package xyz.lebster.core.value;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value) {
		super(value);
	}
}