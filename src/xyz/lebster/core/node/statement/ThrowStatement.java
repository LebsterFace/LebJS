package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;

public record ThrowStatement(Expression value) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(value.execute(interpreter));
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.container(value);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("throw ");
		value.represent(representation);
		representation.appendLine();
	}
}