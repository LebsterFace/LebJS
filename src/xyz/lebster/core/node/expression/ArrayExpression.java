package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.object.ArrayObject;

import java.util.List;

public record ArrayExpression(List<Expression> elements) implements Expression {
	@Override
	public ArrayObject execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] results = new Value[elements.size()];
		for (int i = 0; i < elements.size(); i++)
			results[i] = elements.get(i).execute(interpreter);

		return new ArrayObject(results);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ArrayExpression");
		for (int i = 0; i < elements.size(); i++) {
			Dumper.dumpIndicated(indent + 1, String.valueOf(i), elements.get(i));
		}
	}
}