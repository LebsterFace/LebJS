package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Value;

public record ExecutionContext(Environment environment, Value<?> thisValue) {
}