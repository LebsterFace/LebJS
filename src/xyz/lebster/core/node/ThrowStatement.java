package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Value;

public record ThrowStatement(Expression value) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(value.execute(interpreter));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ThrowStatement");
		value.dump(indent + 1);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("throw ");
		value.represent(representation);
		representation.appendLine();
	}
}