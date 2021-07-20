package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ArrayObject;

import java.util.ArrayList;
import java.util.List;

public record ArrayExpression(List<Expression> elements) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
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