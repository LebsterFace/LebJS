package xyz.lebster.interpreter;

import xyz.lebster.node.value.Value;

public class AbruptCompletion extends Throwable {
	public final Type type;
	public final Value<?> value;

	public AbruptCompletion(Value<?> value, Type type) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "[" + type.name() + "] " + (value == null ? "" : value.toString());
	}

	public enum Type {Return, Throw, Break, Continue}
}