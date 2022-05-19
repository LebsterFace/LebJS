package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;

public class TemplateLiteral implements Expression {
	private final ArrayList<TemplateLiteralNode> backingList = new ArrayList<>();

	public void expressionNode(Expression expression) {
		this.backingList.add(new TemplateLiteralExpressionNode(expression));
	}

	public void spanNode(String string) {
		this.backingList.add(new TemplateLiteralSpanNode(string));
	}

	@Override
	public StringValue execute(Interpreter interpreter) throws AbruptCompletion {
		final StringBuilder builder = new StringBuilder();
		for (final TemplateLiteralNode node : this.backingList) {
			node.append(interpreter, builder);
		}

		return new StringValue(builder.toString());
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.notImplemented(indent, this);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('`');
		for (final TemplateLiteralNode node : this.backingList) {
			if (node instanceof final TemplateLiteralSpanNode spanNode) {
				representation.append(spanNode.string);
			} else if (node instanceof final TemplateLiteralExpressionNode expressionNode) {
				representation.append("${");
				expressionNode.expression.represent(representation);
				representation.append('}');
			} else {
				throw new IllegalStateException();
			}
		}
		representation.append('`');
	}

	private sealed interface TemplateLiteralNode {
		void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion;
	}

	private record TemplateLiteralSpanNode(String string) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) {
			builder.append(string);
		}
	}

	private record TemplateLiteralExpressionNode(Expression expression) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion {
			builder.append(expression.execute(interpreter).toStringValue(interpreter).value);
		}
	}
}
