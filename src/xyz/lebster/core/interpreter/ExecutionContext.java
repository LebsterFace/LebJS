package xyz.lebster.core.interpreter;

import xyz.lebster.core.runtime.value.Value;

public record ExecutionContext(Environment environment, Value<?> thisValue) {
}