package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Value;

public record ReturnStatement(Expression value) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw new AbruptCompletion(value.execute(interpreter), AbruptCompletion.Type.Return);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ReturnStatement");
		value.dump(indent + 1);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("return ");
		value.represent(representation);
		representation.appendLine();
	}
}