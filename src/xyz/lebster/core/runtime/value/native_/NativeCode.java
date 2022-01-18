package xyz.lebster.core.runtime.value.native_;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}