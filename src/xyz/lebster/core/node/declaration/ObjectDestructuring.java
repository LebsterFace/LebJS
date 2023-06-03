package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.literal.StringLiteral;
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
	public void dump(int indent) {
		final var dumper = DumpBuilder.begin(indent)
			.self(this)
			.stringChild("Rest Name", restName);

		if (pairs.isEmpty()) {
			dumper.missing("Children");
			return;
		}

		final var pairsDumper = dumper.nestedChild("Children");
		for (final var entry : pairs.entrySet()) {
			pairsDumper.nestedName("Child")
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
