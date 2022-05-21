package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;

import java.util.ArrayList;
import java.util.List;

public record ArrayAssignmentTarget(AssignmentTarget... children) implements AssignmentTarget {
	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final var iterator = IteratorHelper.getIterator(interpreter, input);

		final ArrayList<BindingPair> result = new ArrayList<>();
		IteratorHelper.IteratorResult next = iterator.next();
		for (final AssignmentTarget child : children) {
			if (child != null) result.addAll(child.getBindings(interpreter, next.value));
			next = iterator.next();
		}

		return result;
	}
}
