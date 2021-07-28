package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Dictionary;

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
			dictionary.set(entry.getKey().execute(interpreter).toStringLiteral(interpreter), entry.getValue().execute(interpreter));
		}

		return dictionary;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "[ObjectExpression]");
	}
}