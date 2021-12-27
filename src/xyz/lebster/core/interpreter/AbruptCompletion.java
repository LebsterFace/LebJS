package xyz.lebster.core.interpreter;

import xyz.lebster.core.node.value.Value;

public final class AbruptCompletion extends Throwable {
	public final Type type;
	public final Value<?> value;

	public AbruptCompletion(Value<?> value, Type type) {
		this.type = type;
		this.value = value;
	}

	public static AbruptCompletion error(Value<?> err) {
		return new AbruptCompletion(err, Type.Throw);
	}

	@Override
	public String getMessage() {
		return "[" + type.name() + "] " + (value == null ? "" : value.toString());
	}

	public enum Type { Throw, Break, Continue }
}