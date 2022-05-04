package xyz.lebster.core;

import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.ExpressionList;
import xyz.lebster.core.runtime.value.Value;

public final class DumpBuilder {
	private final int rootIndentation;

	private DumpBuilder(int indent) {
		this.rootIndentation = indent;
	}

	public static DumpBuilder begin(int indent) {
		return new DumpBuilder(indent);
	}

	public static void single(int indent, String name) {
		Dumper.dumpSingle(indent, name);
	}

	public static void notImplemented(int indent, Dumpable dumpable) {
		Dumper.dumpSingle(indent, ANSI.BACKGROUND_BRIGHT_YELLOW + ANSI.BLACK + '[' + dumpable.getClass().getSimpleName() + ']' + ANSI.RESET);
	}

	public DumpBuilder self(Dumpable obj) {
		Dumper.dumpName(rootIndentation, obj.getClass().getSimpleName());
		return this;
	}

	public DumpBuilder selfNamed(Dumpable self, String name) {
		Dumper.dumpParameterized(rootIndentation, self.getClass().getSimpleName(), name);
		return this;
	}

	public DumpBuilder single(String value) {
		Dumper.dumpSingle(rootIndentation + 1, value);
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

	public DumpBuilder singleChild(String indicator, String value) {
		Dumper.dumpIndicator(rootIndentation, indicator);
		Dumper.dumpSingle(rootIndentation + 1, value);
		return this;
	}

	public DumpBuilder children(String indicator, Iterable<Dumpable> values) {
		Dumper.dumpIndicator(rootIndentation + 1, indicator);
		for (final Dumpable child : values) {
			child.dump(rootIndentation + 2);
		}

		return this;
	}

	public DumpBuilder children(String indicator, Dumpable[] values) {
		Dumper.dumpIndicator(rootIndentation + 1, indicator);
		for (final Dumpable child : values) {
			child.dump(rootIndentation + 2);
		}

		return this;
	}

	public DumpBuilder optional(String indicator, Dumpable node) {
		if (node == null) {
			Dumper.dumpSingle(rootIndentation + 1, "No " + indicator);
		} else {
			Dumper.dumpIndicated(rootIndentation + 1, indicator, node);
		}

		return this;
	}

	public DumpBuilder expressionList(String indicator, ExpressionList expressionList) {
		if (expressionList == null || expressionList.isEmpty()) {
			Dumper.dumpSingle(rootIndentation + 1, "No " + indicator);
		} else {
			Dumper.dumpIndicator(rootIndentation + 1, indicator);
			expressionList.dumpWithoutIndices(rootIndentation + 2);
		}

		return this;
	}

	public void container(ExpressionList expressionList) {
		if (expressionList != null && !expressionList.isEmpty())
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

	public DumpBuilder value(String indicator, Value<?> value) {
		Dumper.dumpIndicator(rootIndentation, indicator);
		Dumper.dumpValue(rootIndentation + 1, value.getClass().getSimpleName(), value.toDisplayString());
		return this;
	}
}