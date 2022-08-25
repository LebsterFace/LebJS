package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.IteratorResult;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.List;

public record ArrayDestructuring(AssignmentTarget restTarget, AssignmentPattern... children) implements AssignmentTarget {
	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final var iterator = IteratorHelper.getIterator(interpreter, input);

		final ArrayList<BindingPair> result = new ArrayList<>();
		IteratorResult next = iterator.next();
		for (final var child : children) {
			if (child != null) {
				final Value<?> v = next.done() ? Undefined.instance : next.value();
				final Value<?> x = v == Undefined.instance && child.defaultExpression() != null ?
					child.defaultExpression().execute(interpreter) : v;
				result.addAll(child.assignmentTarget().getBindings(interpreter, x));
			}

			next = iterator.next();
		}

		if (restTarget != null) {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			while (!next.done()) {
				restValues.add(next.value());
				next = iterator.next();
			}

			final ArrayObject restArray = new ArrayObject(interpreter, restValues);
			result.addAll(restTarget.getBindings(interpreter, restArray));
		}

		return result;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.optional("Rest Target", restTarget)
			.children("Elements", children);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('[');
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null) children[i].represent(representation);
			if (restTarget != null || i + 1 < children.length) representation.append(", ");
		}

		if (restTarget != null) {
			representation.append("...");
			restTarget.represent(representation);
		}

		representation.append(']');
	}
}
