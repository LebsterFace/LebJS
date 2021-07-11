package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.expression.Expression;
import xyz.lebster.node.value.Value;

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