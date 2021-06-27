package xyz.lebster.core.runtime;

import xyz.lebster.core.value.Value;

// https://tc39.es/ecma262/multipage/ecmascript-data-types-and-values.html#sec-completion-record-specification-type
// FIXME: Labels
public final class AbruptCompletion extends Throwable {
	public final Type type;
	public final Value<?> value;

	public AbruptCompletion(Type type, Value<?> value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String getMessage() {
		return "[" + type.name() + "] " + value.toString();
	}

	public enum Type {
		Break, Continue, Return, Throw
	}
}
