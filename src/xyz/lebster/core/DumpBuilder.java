package xyz.lebster.core;

import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.ExpressionList;
import xyz.lebster.core.value.Value;

@SuppressWarnings("UnusedReturnValue")
public final class DumpBuilder {
	private final int rootIndentation;

	private DumpBuilder(int indent) {
		this.rootIndentation = indent;
	}

	public static DumpBuilder begin(int indent) {
		return new DumpBuilder(indent);
	}

	public DumpBuilder self(Dumpable obj) {
		Dumper.dumpName(rootIndentation, obj.getClass().getSimpleName());
		return this;
	}

	public DumpBuilder selfParameterized(Dumpable self, String name) {
		Dumper.dumpParameterized(rootIndentation, self.getClass().getSimpleName(), name);
		return this;
	}

	public DumpBuilder enum_(String name, Enum<?> value) {
		Dumper.dumpEnum(rootIndentation + 1, name, value);
		return this;
	}

	public DumpBuilder operator(Enum<?> value) {
		return this.enum_("Operator", value);
	}

	public DumpBuilder child(String indicator, Dumpable value) {
		Dumper.dumpIndicated(rootIndentation + 1, indicator, value);
		return this;
	}

	public DumpBuilder stringChild(String indicator, String value) {
		Dumper.dumpIndicator(rootIndentation + 1, indicator);
		Dumper.dumpString(rootIndentation + 2, value);
		return this;
	}

	public <E extends Dumpable> DumpBuilder children(String indicator, Iterable<E> values) {
		final var iterator = values.iterator();
		if (iterator.hasNext()) {
			Dumper.dumpIndicator(rootIndentation + 1, indicator);
			while (iterator.hasNext()) {
				iterator.next().dump(rootIndentation + 2);
			}
		} else {
			Dumper.dumpString(rootIndentation + 1, "No " + indicator);
		}

		return this;
	}

	public DumpBuilder children(String indicator, Dumpable[] values) {
		if (values.length == 0) {
			Dumper.dumpString(rootIndentation + 1, "No " + indicator);
		} else {
			Dumper.dumpIndicator(rootIndentation + 1, indicator);
			for (final Dumpable child : values) {
				child.dump(rootIndentation + 2);
			}
		}

		return this;
	}

	public DumpBuilder optional(String indicator, Dumpable node) {
		if (node == null) {
			Dumper.dumpString(rootIndentation + 1, "No " + indicator);
		} else {
			Dumper.dumpIndicated(rootIndentation + 1, indicator, node);
		}

		return this;
	}

	public DumpBuilder expressionList(String indicator, ExpressionList expressionList) {
		if (expressionList == null || expressionList.isEmpty()) {
			Dumper.dumpString(rootIndentation + 1, "No " + indicator);
		} else {
			Dumper.dumpIndicator(rootIndentation + 1, indicator);
			expressionList.dumpWithoutIndices(rootIndentation + 2);
		}

		return this;
	}

	public void container(ExpressionList expressionList) {
		if (expressionList == null || expressionList.isEmpty()) return;
		expressionList.dumpWithoutIndices(rootIndentation + 1);
	}

	public DumpBuilder optionalHidden(String indicator, Dumpable node) {
		if (node != null) Dumper.dumpIndicated(rootIndentation + 1, indicator, node);
		return this;
	}

	public void container(Dumpable value) {
		value.dump(rootIndentation + 1);
	}

	public DumpBuilder optionalContainer(Dumpable node) {
		if (node != null) node.dump(rootIndentation + 1);
		return this;
	}

	public void binaryExpression(Dumpable self, Dumpable left, Enum<?> operator, Dumpable right) {
		self(self);
		child("Left", left);
		operator(operator);
		child("Right", right);
	}

	public void value(Dumpable self, String data) {
		Dumper.dumpValue(rootIndentation, self.getClass().getSimpleName(), data);
	}

	public DumpBuilder optionalValue(String name, Value<?> value) {
		if (value == null) {
			Dumper.dumpString(rootIndentation + 1, "No " + name);
		} else {
			final var representation = new StringRepresentation();
			value.displayForConsoleLog(representation);
			Dumper.dumpValue(rootIndentation + 1, name, representation.toString());
		}

		return this;
	}
}