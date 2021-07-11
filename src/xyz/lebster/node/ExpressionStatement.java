package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.expression.Expression;
import xyz.lebster.node.value.Value;

public record ExpressionStatement(Expression expression) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ExpressionStatement");
		expression.dump(indent + 1);
	}

	@Override
	public void represent(StringRepresentation representation) {
		expression.represent(representation);
		representation.appendLine(";");
	}
}