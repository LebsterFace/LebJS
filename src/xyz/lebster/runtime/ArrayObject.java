package xyz.lebster.runtime;

import xyz.lebster.node.value.*;
import xyz.lebster.runtime.prototype.ArrayPrototype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayObject extends Dictionary {
	public final static StringLiteral length = new StringLiteral("length");

	public ArrayObject(List<Value<?>> expressionList) {
		super(getMapFromExpressionList(expressionList));
		this.initialize();
	}

	public ArrayObject() {
		super();
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