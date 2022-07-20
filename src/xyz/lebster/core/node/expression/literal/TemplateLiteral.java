package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.parser.StringEscapeUtils;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;
import java.util.List;

public class TemplateLiteral implements Expression {
	private final List<TemplateLiteralNode> backingList = new ArrayList<>();

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
		DumpBuilder.begin(indent)
			.self(this)
			.children("Nodes", backingList);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('`');
		for (final TemplateLiteralNode node : this.backingList) {
			node.represent(representation);
		}
		representation.append('`');
	}

	private sealed interface TemplateLiteralNode extends Dumpable {
		void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion;
	}

	private record TemplateLiteralSpanNode(String string) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) {
			builder.append(string);
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.value(this, StringEscapeUtils.quote(string, false));
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append(string);
		}
	}

	private record TemplateLiteralExpressionNode(Expression expression) implements TemplateLiteralNode {
		@Override
		public void append(Interpreter interpreter, StringBuilder builder) throws AbruptCompletion {
			builder.append(expression.execute(interpreter).toStringValue(interpreter).value);
		}

		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.child("Expression", expression);
		}

		@Override
		public void represent(StringRepresentation representation) {
			representation.append("${");
			expression.represent(representation);
			representation.append('}');
		}
	}
}
