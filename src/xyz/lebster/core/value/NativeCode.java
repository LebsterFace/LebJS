package xyz.lebster.core.value;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.exception.LanguageException;

@FunctionalInterface
public interface NativeCode {
	Value<?> execute(Interpreter interpreter, Value<?>[] arguments) throws LanguageException;
}