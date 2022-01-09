package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.ObjectValue;

import java.util.HashMap;
import java.util.Map;

public record ObjectExpression(Map<Expression, Expression> entries) implements Expression {
	public ObjectExpression() {
		this(new HashMap<>());
	}

	@Override
	public ObjectValue execute(Interpreter interpreter) throws AbruptCompletion {
		final var map = new HashMap<ObjectValue.Key<?>, Value<?>>();

		for (final Map.Entry<Expression, Expression> entry : entries.entrySet()) {
			final StringValue key = entry.getKey().execute(interpreter).toStringValue(interpreter);
			final Value<?> value = entry.getValue().execute(interpreter);
			map.put(key, value);
		}

		return new ObjectValue(map);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpSingle(indent, "[ObjectExpression]");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("{ ");

		for (var iterator = this.entries.entrySet().iterator(); iterator.hasNext(); ) {
			final var entry = iterator.next();
			entry.getKey().represent(representation);
			representation.append(": ");
			final var value = entry.getValue();
			value.represent(representation);
			if (iterator.hasNext()) representation.append(',');
			representation.append(' ');
		}

		representation.append('}');
	}
}