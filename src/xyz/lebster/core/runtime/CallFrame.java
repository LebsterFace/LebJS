package xyz.lebster.core.runtime;

import xyz.lebster.core.value.Value;

public record CallFrame(Value<?> thisValue) {
}
