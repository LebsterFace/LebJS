package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.IteratorHelper;

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
		for (final ExpressionNode node : backingList) {
			if (node.type == ExpressionNode.Type.SINGLE) {
				result.add(node.expression.execute(interpreter));
			} else {
				IteratorHelper.getIterator(interpreter, node.expression).collect(result);
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
				expression.dump(indent + 1);
			} else {
				expression.dump(indent);
			}
		}

		public void represent(StringRepresentation representation) {
			if (type == Type.SPREAD) representation.append("...");
			expression.represent(representation);
		}

		private enum Type { SINGLE, SPREAD }
	}
}


