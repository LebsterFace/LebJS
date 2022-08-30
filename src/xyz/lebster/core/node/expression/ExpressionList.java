package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;

import java.util.ArrayList;
import java.util.Iterator;

public final class ExpressionList {
	private final ArrayList<ExpressionNode> backingList;
	private final boolean canHaveEmpty;

	public ExpressionList(boolean canHaveEmpty) {
		this.canHaveEmpty = canHaveEmpty;
		this.backingList = new ArrayList<>();
	}

	public void addSingleExpression(Expression expression) {
		this.backingList.add(new ExpressionNode(expression, ExpressionNode.Type.SINGLE));
	}

	public void addSpreadExpression(Expression expressions) {
		this.backingList.add(new ExpressionNode(expressions, ExpressionNode.Type.SPREAD));
	}

	public void addEmpty() {
		if (this.canHaveEmpty) {
			this.backingList.add(new ExpressionNode(null, ExpressionNode.Type.EMPTY));
		} else {
			throw new ShouldNotHappen("Attempting to add an empty node to an ExpressionList which cannot have empty nodes");
		}
	}

	public ArrayList<Value<?>> executeAll(Interpreter interpreter) throws AbruptCompletion {
		final ArrayList<Value<?>> result = new ArrayList<>(backingList.size());
		for (final ExpressionNode node : backingList) {
			switch (node.type) {
				case SINGLE -> result.add(node.expression.execute(interpreter));
				case EMPTY -> result.add(null);
				case SPREAD -> IteratorHelper.getIterator(interpreter, node.expression).collect(result);
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
			} else if (type == Type.EMPTY) {
				Dumper.dumpSingle(indent, "Empty");
			} else {
				expression.dump(indent);
			}
		}

		public void represent(StringRepresentation representation) {
			if (type == Type.EMPTY) {
				representation.append(' ');
				return;
			}

			if (type == Type.SPREAD) representation.append("...");
			expression.represent(representation);
		}

		private enum Type { SINGLE, EMPTY, SPREAD }
	}
}


