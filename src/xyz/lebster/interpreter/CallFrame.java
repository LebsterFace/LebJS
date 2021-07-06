package xyz.lebster.interpreter;

import xyz.lebster.node.value.Value;

public record CallFrame(Value<?> executedCallee, Value<?> thisValue) {
}
