package xyz.lebster.core.node.declaration;

import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

public record BindingPair(StringValue name, Value<?> value) {
}
