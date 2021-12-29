package xyz.lebster.core.runtime;

import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.prototype.ArrayPrototype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArrayObject extends Dictionary {
	public final static StringLiteral length = new StringLiteral("length");

	public ArrayObject(List<Value<?>> expressionList) {
		super(getMapFromExpressionList(expressionList));
		this.initialize();
	}

	private static Map<StringLiteral, Value<?>> getMapFromExpressionList(List<Value<?>> expressionList) {
		final Map<StringLiteral, Value<?>> result = new HashMap<>(expressionList.size());
		for (int i = 0; i < expressionList.size(); i++)
			result.put(new StringLiteral(String.valueOf(i)), expressionList.get(i));

		return result;
	}

	private void initialize() {
		this.set(length, new NumericLiteral(value.size()));
	}

	@Override
	public Dictionary getPrototype() {
		return ArrayPrototype.instance;
	}
}