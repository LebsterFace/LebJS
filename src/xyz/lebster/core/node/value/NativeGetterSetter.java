package xyz.lebster.core.node.value;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

public interface NativeGetterSetter {
	Value<?> get(Interpreter interpreter) throws AbruptCompletion;

	void set(Interpreter interpreter, Value<?> value) throws AbruptCompletion;
}
