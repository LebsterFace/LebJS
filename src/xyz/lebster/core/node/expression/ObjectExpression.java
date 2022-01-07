package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.ObjectLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;

import java.util.HashMap;
import java.util.Map;

public record ObjectExpression(Map<Expression, Expression> entries) implements Expression {
	public ObjectExpression() {
		this(new HashMap<>());
	}

	@Override
	public ObjectLiteral execute(Interpreter interpreter) throws AbruptCompletion {
		final var map = new HashMap<ObjectLiteral.Key<?>, Value<?>>();

		for (final Map.Entry<Expression, Expression> entry : entries.entrySet()) {
			final StringLiteral key = entry.getKey().execute(interpreter).toStringLiteral(interpreter);
			final Value<?> value = entry.getValue().execute(interpreter);
			map.put(key, value);
		}

		return new ObjectLiteral(map);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "[ObjectExpression]");
	}
}