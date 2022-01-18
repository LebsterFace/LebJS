package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public abstract class Constructor<V> extends Executable<V> {
	public Constructor(V code) {
		super(code);
	}

	public abstract ObjectValue construct(Interpreter interpreter, Value<?>[] executedArguments) throws AbruptCompletion;
}