package xyz.lebster.core.value;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;


public abstract class Executable<JType> extends Value<JType> {
	public Executable(Type type, JType value) {
		super(type, value);
	}

	abstract public Value<?> executeChildren(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
