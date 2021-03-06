package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;

public record ExpressionStatement(Expression expression) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.container(expression);
	}

	@Override
	public void represent(StringRepresentation representation) {
		expression.represent(representation);
		representation.append(";");
	}
}