package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Value;

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

	public String getValue() {
		return value == null ? "" : value.toString();
	}

	@Override
	public String getMessage() {
		return "[" + type.name() + "] " + getValue();
	}

	public enum Type { Return, Throw, Break, Continue }
}