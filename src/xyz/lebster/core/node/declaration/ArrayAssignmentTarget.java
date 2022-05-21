package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.List;

public record ArrayAssignmentTarget(AssignmentTarget restTarget, AssignmentTarget... children) implements AssignmentTarget {
	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final var iterator = IteratorHelper.getIterator(interpreter, input);

		final ArrayList<BindingPair> result = new ArrayList<>();
		IteratorHelper.IteratorResult next = iterator.next();
		for (final AssignmentTarget child : children) {
			if (child != null) {
				final Value<?> v = next.done ? Undefined.instance : next.value;
				result.addAll(child.getBindings(interpreter, v));
			}

			next = iterator.next();
		}

		if (restTarget != null) {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			while (!next.done) {
				restValues.add(next.value);
				next = iterator.next();
			}

			final ArrayObject restArray = new ArrayObject(interpreter, restValues);
			result.addAll(restTarget.getBindings(interpreter, restArray));
		}

		return result;
	}
}
