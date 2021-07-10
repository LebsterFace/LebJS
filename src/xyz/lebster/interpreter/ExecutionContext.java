package xyz.lebster.interpreter;

import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.LexicalEnvironment;

public record ExecutionContext(LexicalEnvironment environment, Value<?> executedCallee, Value<?> thisValue) {
}