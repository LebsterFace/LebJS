package xyz.lebster.core.node.value;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}