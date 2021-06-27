package xyz.lebster.core.value;

import xyz.lebster.core.runtime.Interpreter;


@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments);
}