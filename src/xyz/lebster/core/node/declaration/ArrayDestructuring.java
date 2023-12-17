package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.ArrayList;

import static xyz.lebster.core.value.iterator.IteratorPrototype.*;

public record ArrayDestructuring(AssignmentTarget restTarget, AssignmentPattern... children) implements AssignmentTarget {
	@Override
	public Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		final var iterator = getIterator(interpreter, value);

		ObjectValue iterResult = iterator.next(interpreter, null);
		for (final var child : children) {
			if (child != null) {
				final Value<?> v = iteratorValue(interpreter, iterResult);
				child.assign(interpreter, v);
			}

			iterResult = iterator.next(interpreter, null);
		}

		if (restTarget != null) {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			while (!iteratorComplete(interpreter, iterResult)) {
				restValues.add(iteratorValue(interpreter, iterResult));
				iterResult = iterator.next(interpreter, null);
			}

			restTarget.assign(interpreter, new ArrayObject(interpreter, restValues));
		}

		return value;
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> value) throws AbruptCompletion {
		final var iterator = getIterator(interpreter, value);

		ObjectValue iterResult = iterator.next(interpreter, null);
		for (final var child : children) {
			if (child != null) {
				final Value<?> v = iteratorValue(interpreter, iterResult);
				child.declare(interpreter, kind, v);
			}

			iterResult = iterator.next(interpreter, null);
		}

		if (restTarget != null) {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			while (!iteratorComplete(interpreter, iterResult)) {
				restValues.add(iteratorValue(interpreter, iterResult));
				iterResult = iterator.next(interpreter, null);
			}

			final ArrayObject restArray = new ArrayObject(interpreter, restValues);
			restTarget.declare(interpreter, kind, restArray);
		}
	}
}
