package xyz.lebster.node.value;

public abstract class Primitive<JType> extends Value<JType> {
	public Primitive(JType value, Type type) {
		super(value, type);
	}
}
