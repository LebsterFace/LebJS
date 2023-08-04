package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.List;

public record TemplateLiteral(SourceRange range, List<TemplateLiteralNode> backingList) implements Expression {
	@Override
	public StringValue execute(Interpreter interpreter) throws AbruptCompletion {
		final StringBuilder builder = new StringBuilder();
		for (final TemplateLiteralNode node : this.backingList) {
			node.append(interpreter, builder);
		}

		return new StringValue(builder.toString());
	}

	public sealed interface TemplateLiteralNode {
		void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion;
	}

	public record TemplateLiteralSpanNode(String string) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) {
			builder.append(string);
		}
	}

	public record TemplateLiteralExpressionNode(Expression expression) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion {
			builder.append(expression.execute(interpreter).toStringValue(interpreter).value);
		}
	}
}
