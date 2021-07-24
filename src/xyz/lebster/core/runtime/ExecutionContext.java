package xyz.lebster.core.runtime;

import xyz.lebster.core.node.value.Value;

public record ExecutionContext(Value<?> executedCallee, Value<?> thisValue) {
}