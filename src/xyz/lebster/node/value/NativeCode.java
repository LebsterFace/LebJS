package xyz.lebster.node.value;


import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}