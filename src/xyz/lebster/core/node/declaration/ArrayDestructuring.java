package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.ArrayList;
import java.util.List;

public record ArrayDestructuring(AssignmentTarget restTarget, AssignmentPattern... children) implements AssignmentTarget {
	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion {
		final var iterator = IteratorHelper.getIterator(interpreter, input);

		final ArrayList<BindingPair> result = new ArrayList<>();
		ObjectValue iterResult = iterator.next(interpreter, null);
		for (final var child : children) {
			if (child != null) {
				final Value<?> v = IteratorHelper.iteratorValue(interpreter, iterResult);
				final Value<?> x = v != Undefined.instance || child.defaultExpression() == null ?
					v : child.defaultExpression().execute(interpreter);
				result.addAll(child.assignmentTarget().getBindings(interpreter, x));
			}

			iterResult = iterator.next(interpreter, null);
		}

		if (restTarget != null) {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			while (!IteratorHelper.iteratorComplete(interpreter, iterResult)) {
				restValues.add(IteratorHelper.iteratorValue(interpreter, iterResult));
				iterResult = iterator.next(interpreter, null);
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
