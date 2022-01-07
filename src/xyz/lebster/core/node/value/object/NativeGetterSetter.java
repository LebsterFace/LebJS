package xyz.lebster.core.node.value.object;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;

public interface NativeGetterSetter {
	Value<?> get(Interpreter interpreter) throws AbruptCompletion;

	void set(Interpreter interpreter, Value<?> value) throws AbruptCompletion;
}
