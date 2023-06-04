package xyz.lebster.core;

import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.ExpressionList;
import xyz.lebster.core.value.primitive.string.StringValue;

@SuppressWarnings("UnusedReturnValue")
public final class DumpBuilder {
	private final int indentation;

	private DumpBuilder(int indent) {
		this.indentation = indent;
	}

	public static DumpBuilder begin(int indent) {
		return new DumpBuilder(indent);
	}

	public DumpBuilder self(Dumpable obj) {
		Dumper.dumpName(indentation, obj.getClass().getSimpleName());
		return this;
	}

	public DumpBuilder selfParameterized(Dumpable self, String name) {
		Dumper.dumpParameterized(indentation, self.getClass().getSimpleName(), name);
		return this;
	}

	public DumpBuilder operator(Enum<?> value) {
		return enumChild("Operator", value);
	}

	public DumpBuilder child(String indicator, Dumpable node) {
		Dumper.dumpIndicator(indentation + 1, indicator);
		node.dump(indentation + 2);
		return this;
	}

	public DumpBuilder hiddenChild(String indicator, Dumpable node) {
		return node == null ? this : child(indicator, node);
	}

	public DumpBuilder optionalChild(String indicator, Dumpable node) {
		return node == null ? missing(indicator) : child(indicator, node);
	}

	public DumpBuilder stringChild(String name, StringValue string) {
		if (string == null) return missing(name);
		Dumper.dumpValue(indentation + 1, name, string.value);
		return this;
	}

	public DumpBuilder nestedChild(String name) {
		Dumper.dumpIndicator(indentation + 1, name);
		return DumpBuilder.begin(indentation + 1);
	}

	public DumpBuilder enumChild(String name, Enum<?> value) {
		Dumper.dumpIndicator(indentation + 1, name);
		Dumper.dumpEnum(indentation + 2, value);
		return this;
	}

	public DumpBuilder nestedName(String name) {
		Dumper.dumpName(indentation + 1, name);
		return DumpBuilder.begin(indentation + 1);
	}

	public DumpBuilder missing(String name) {
		Dumper.dumpString(indentation + 1, "No " + name);
		return this;
	}

	public <E extends Dumpable> DumpBuilder children(String indicator, Iterable<E> values) {
		final var iterator = values.iterator();
		if (!iterator.hasNext()) return missing(indicator);

		Dumper.dumpIndicator(indentation + 1, indicator);
		while (iterator.hasNext()) {
			iterator.next().dump(indentation + 2);
		}

		return this;
	}

	public DumpBuilder children(String indicator, Dumpable[] values) {
		if (values.length == 0) return missing(indicator);

		Dumper.dumpIndicator(indentation + 1, indicator);
		for (final Dumpable child : values) {
			child.dump(indentation + 2);
		}

		return this;
	}

	public DumpBuilder expressionList(String indicator, ExpressionList expressionList) {
		if (expressionList == null || expressionList.isEmpty()) return missing(indicator);

		Dumper.dumpIndicator(indentation + 1, indicator);
		expressionList.dump(indentation + 2);
		return this;
	}

	public void container(Dumpable node) {
		node.dump(indentation + 1);
	}

	public void container(ExpressionList expressionList) {
		if (expressionList == null || expressionList.isEmpty()) missing("Children");
		expressionList.dump(indentation + 1);
	}

	public DumpBuilder optionalContainer(Dumpable node) {
		if (node != null) container(node);
		return this;
	}

	public void binaryExpression(Dumpable self, Dumpable left, Enum<?> operator, Dumpable right) {
		self(self);
		child("Left", left);
		operator(operator);
		child("Right", right);
	}

	public void selfValue(Dumpable self) {
		Dumper.dumpValue(indentation, self.getClass().getSimpleName());
	}

	public void selfValue(Dumpable self, String data) {
		Dumper.dumpValue(indentation, self.getClass().getSimpleName(), data);
	}

	public void selfString(Dumpable self) {
		Dumper.dumpString(indentation, self.getClass().getSimpleName());
	}
}