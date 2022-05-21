package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

import java.util.List;

public record IdentifierAssignmentTarget(StringValue name) implements AssignmentTarget {
	public IdentifierAssignmentTarget(String name) {
		this(new StringValue(name));
	}

	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) {
		return List.of(new BindingPair(name, input));
	}
}