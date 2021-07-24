package xyz.lebster.core.interpreter;

import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.LexicalEnvironment;

public record ExecutionContext(LexicalEnvironment environment, Value<?> executedCallee, Value<?> thisValue) {
}