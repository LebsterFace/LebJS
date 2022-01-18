package xyz.lebster.core.interpreter;

import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.LexicalEnvironment;

public record ExecutionContext(LexicalEnvironment environment, Value<?> executedCallee, Value<?> thisValue) {
	public ExecutionContext boundTo(Value<?> value) {
		return new ExecutionContext(environment, executedCallee, value);
	}
}