package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

public abstract class Constructor<V> extends Executable<V> {
	public Constructor(V code) {
		super(code);
	}

	public abstract Instance construct(Interpreter interpreter, Value<?>[] executedArguments) throws AbruptCompletion;
}