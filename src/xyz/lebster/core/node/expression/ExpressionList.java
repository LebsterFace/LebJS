package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;

import java.util.ArrayList;
import java.util.Iterator;

public final class ExpressionList {
	private final ArrayList<ExpressionNode> backingList;

	public ExpressionList() {
		this.backingList = new ArrayList<>();
	}

	public void addSingleExpression(Expression expression) {
		this.backingList.add(new ExpressionNode(expression, ExpressionNode.Type.SINGLE));
	}

	public void addSpreadExpression(Expression expressions) {
		this.backingList.add(new ExpressionNode(expressions, ExpressionNode.Type.SPREAD));
	}

	public ArrayList<Value<?>> executeAll(Interpreter interpreter) throws AbruptCompletion {
		final ArrayList<Value<?>> result = new ArrayList<>(backingList.size());
		for (final ExpressionNode expressionNode : backingList) {
			final Value<?> value = expressionNode.expression.execute(interpreter);
			if (expressionNode.type == ExpressionNode.Type.SINGLE) {
				result.add(value);
				continue;
			}


			// TODO: Clean up with helper methods
			final ObjectValue.IteratorRecord record = value.toObjectValue(interpreter).getIterator(interpreter);
			if (record == null) {
				final var representation = new StringRepresentation();
				expressionNode.expression.represent(representation);
				representation.append(" is not iterable");
				throw AbruptCompletion.error(new TypeError(representation.toString()));
			}

			while (true) {
				// Call .next()
				final Executable<?> executable = Executable.getExecutable(record.nextMethod());
				final Value<?> nextResult = executable.call(interpreter, record.iterator());

				// If the return value of .next() is not a { done: boolean; value: any; }, throw
				if (!(nextResult instanceof ObjectValue next))
					throw AbruptCompletion.error(new TypeError("Iterator result is not an object"));

				// Stop if .done = true
				if (next.get(interpreter, Names.done).isTruthy(interpreter))
					break;

				final Value<?> nextValue = next.get(interpreter, Names.value);
				result.add(nextValue);
			}
		}

		return result;
	}

	public void dumpWithIndices(int indent) {
		for (int i = 0; i < backingList.size(); i++) {
			Dumper.dumpIndicator(indent, String.valueOf(i));
			backingList.get(i).dump(indent + 1);
		}
	}

	public void dumpWithoutIndices(int indent) {
		for (final ExpressionNode node : backingList) {
			node.dump(indent + 1);
		}
	}

	public void represent(StringRepresentation representation) {
		final Iterator<ExpressionNode> iterator = backingList.iterator();
		while (iterator.hasNext()) {
			final ExpressionNode element = iterator.next();
			element.represent(representation);
			if (iterator.hasNext()) representation.append(", ");
		}
	}

	public boolean isEmpty() {
		return backingList.isEmpty();
	}

	private record ExpressionNode(Expression expression, Type type) {
		public void dump(int indent) {
			if (type == Type.SPREAD) {
				Dumper.dumpIndicator(indent, "Spread");
				indent++;
			}

			expression.dump(indent);
		}

		public void represent(StringRepresentation representation) {
			if (type == Type.SPREAD) {
				representation.append("...");
			}

			expression.represent(representation);
		}

		private enum Type { SINGLE, SPREAD }
	}
}


