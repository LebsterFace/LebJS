package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.Dictionary;

import java.util.HashMap;
import java.util.Map;

public record ObjectExpression(Map<Expression, Expression> entries) implements Expression {
	public ObjectExpression() {
		this(new HashMap<>());
	}

	@Override
	public Dictionary execute(Interpreter interpreter) throws AbruptCompletion {
		final Dictionary dictionary = new Dictionary();
		for (final Map.Entry<Expression, Expression> entry : entries.entrySet()) {
			dictionary.set(entry.getKey().execute(interpreter).toStringLiteral(), entry.getValue().execute(interpreter));
		}

		return dictionary;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "[ObjectExpression]");
	}
}