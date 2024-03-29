package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record ObjectDestructuring(Map<Expression, AssignmentPattern> pairs, StringValue restName) implements AssignmentTarget {
	@Override
	public Value<?> assign(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final ObjectValue objectValue = input.toObjectValue(interpreter);

		final Set<Key<?>> visitedKeys = new HashSet<>();
		for (final var entry : pairs.entrySet()) {
			final Key<?> key = entry.getKey().execute(interpreter).toPropertyKey(interpreter);
			final Value<?> property = objectValue.get(interpreter, key);
			entry.getValue().assign(interpreter, property);
			visitedKeys.add(key);
		}

		if (restName != null) {
			final ObjectValue restObject = new ObjectValue(interpreter.intrinsics);
			for (final Key<?> key : objectValue.ownPropertyKeys()) {
				final PropertyDescriptor value = objectValue.getOwnProperty(key);
				if (!visitedKeys.contains(key) && value.isEnumerable()) {
					restObject.put(key, objectValue.get(interpreter, key));
				}
			}

			interpreter.getBinding(restName).putValue(interpreter, restObject);
		}

		return input;
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> input) throws AbruptCompletion {
		final ObjectValue objectValue = input.toObjectValue(interpreter);

		final Set<Key<?>> visitedKeys = new HashSet<>();
		for (final var entry : pairs.entrySet()) {
			final Key<?> key = entry.getKey().execute(interpreter).toPropertyKey(interpreter);
			final Value<?> property = objectValue.get(interpreter, key);
			entry.getValue().declare(interpreter, kind, property);
			visitedKeys.add(key);
		}

		if (restName != null) {
			final ObjectValue restObject = new ObjectValue(interpreter.intrinsics);
			for (final Key<?> key : objectValue.ownPropertyKeys()) {
				final PropertyDescriptor value = objectValue.getOwnProperty(key);
				if (!visitedKeys.contains(key) && value.isEnumerable()) {
					restObject.put(key, objectValue.get(interpreter, key));
				}
			}

			interpreter.declareVariable(kind, restName, restObject);
		}
	}
}
