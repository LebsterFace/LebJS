package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.*;

public record ObjectAssignmentTarget(Map<Expression, AssignmentTarget> pairs, StringValue spreadName) implements AssignmentTarget {
	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final ObjectValue objectValue = input.toObjectValue(interpreter);

		final Set<ObjectValue.Key<?>> visitedKeys = new HashSet<>();
		final ArrayList<BindingPair> result = new ArrayList<>();
		for (final var entry : pairs.entrySet()) {
			final ObjectValue.Key<?> key = entry.getKey().execute(interpreter).toPropertyKey(interpreter);
			final Value<?> property = objectValue.get(interpreter, key);
			result.addAll(entry.getValue().getBindings(interpreter, property));
			visitedKeys.add(key);
		}

		if (spreadName != null) {
			final ObjectValue spreadObject = new ObjectValue(interpreter.intrinsics.objectPrototype);
			for (final var entry : objectValue.entries()) {
				final ObjectValue.Key<?> key = entry.getKey();
				if (!visitedKeys.contains(key) && entry.getValue().isEnumerable()) {
					spreadObject.put(key, objectValue.get(interpreter, key));
				}
			}

			result.add(new BindingPair(spreadName, spreadObject));
		}

		return result;
	}
}
