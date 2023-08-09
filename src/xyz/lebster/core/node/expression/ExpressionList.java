package xyz.lebster.core.node.expression;

import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;

import java.util.ArrayList;

public record ExpressionList(boolean canHaveEmpty, ArrayList<ExpressionNode> backingList) {
	public ExpressionList(boolean canHaveEmpty) {
		this(canHaveEmpty, new ArrayList<>());
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

	public Value<?>[] executeAll(Interpreter interpreter) throws AbruptCompletion {
		final ArrayList<Value<?>> result = new ArrayList<>(backingList.size());
		for (final ExpressionNode node : backingList) {
			switch (node.type) {
				case SINGLE -> result.add(node.expression.execute(interpreter));
				case EMPTY -> result.add(null);
				case SPREAD -> IteratorHelper.getIterator(interpreter, node.expression).collect(interpreter, result);
			}
		}

		return result.toArray(new Value[0]);
	}

	public boolean isEmpty() {
		return backingList.isEmpty();
	}

	private record ExpressionNode(Expression expression, Type type) {
		private enum Type { SINGLE, EMPTY, SPREAD }
	}
}


