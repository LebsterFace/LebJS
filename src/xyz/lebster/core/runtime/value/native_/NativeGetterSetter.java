package xyz.lebster.core.runtime.value.native_;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;

public interface NativeGetterSetter {
	Value<?> get(Interpreter interpreter);

	void set(Interpreter interpreter, Value<?> value) throws AbruptCompletion;
}
