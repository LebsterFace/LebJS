package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.ArrayObject;

import java.util.ArrayList;
import java.util.List;

public record ArrayExpression(List<Expression> elements) implements Expression {
	@Override
	public ArrayObject execute(Interpreter interpreter) throws AbruptCompletion {
		final List<Value<?>> results = new ArrayList<>(elements.size());
		for (Expression element : elements) results.add(element.execute(interpreter));
		return new ArrayObject(results);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ArrayExpression");
		for (int i = 0; i < elements.size(); i++) {
			Dumper.dumpIndicated(indent + 1, "i", elements.get(i));
		}
	}
}