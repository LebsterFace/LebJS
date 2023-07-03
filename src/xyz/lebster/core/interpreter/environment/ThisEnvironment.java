package xyz.lebster.core.interpreter.environment;

import xyz.lebster.core.value.Value;

public interface ThisEnvironment extends Environment {
	Value<?> thisValue();
}
