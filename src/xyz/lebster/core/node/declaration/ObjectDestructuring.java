package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.*;

public record ObjectDestructuring(Map<Expression, AssignmentTarget> pairs, StringValue restName) implements AssignmentTarget {
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

		if (restName != null) {
			final ObjectValue restObject = new ObjectValue(interpreter.intrinsics.objectPrototype);
			for (final var entry : objectValue.entries()) {
				final ObjectValue.Key<?> key = entry.getKey();
				if (!visitedKeys.contains(key) && entry.getValue().isEnumerable()) {
					restObject.put(key, objectValue.get(interpreter, key));
				}
			}

			result.add(new BindingPair(restName, restObject));
		}

		return result;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.optionalValue("Rest Name", restName);
		Dumper.dumpIndicator(indent + 1, "Children");
		for (final var entry : pairs.entrySet()) {
			DumpBuilder.begin(indent + 2)
				.child("Key", entry.getKey())
				.child("Value", entry.getValue());
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("{ ");
		final var iterator = pairs.entrySet().iterator();
		while (iterator.hasNext()) {
			final var entry = iterator.next();
			// TODO: Different types of keys to store this info (similar/(identical?) to ObjectExpression)
			if (entry.getKey() instanceof final StringLiteral literal) {
				literal.value().displayForObjectKey(representation);
			} else {
				representation.append('[');
				entry.getKey().represent(representation);
				representation.append(']');
			}

			representation.append(": ");
			// TODO: Represent shorthands as shorthands
			entry.getValue().represent(representation);
			if (iterator.hasNext() || restName != null) representation.append(',');
			representation.append(' ');
		}

		if (restName != null) {
			representation.append("...");
			representation.append(restName.value);
			representation.append(' ');
		}

		representation.append('}');
	}
}
