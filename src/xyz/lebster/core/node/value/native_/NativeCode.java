package xyz.lebster.core.node.value.native_;


import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}