package xyz.lebster.core.value.function;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}