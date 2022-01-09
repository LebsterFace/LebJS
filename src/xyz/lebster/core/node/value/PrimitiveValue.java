package xyz.lebster.core.node.value;

public abstract class PrimitiveValue<JType> extends Value<JType> {
	public PrimitiveValue(JType value, Type type) {
		super(value, type);
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(value);
	}
}