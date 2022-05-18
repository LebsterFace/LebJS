package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Function;

public record FunctionExpression(BlockStatement body, String name, String[] arguments) implements FunctionNode, Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Function(interpreter, interpreter.lexicalEnvironment(), this);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.selfNamed(this, toCallString())
			.child("Body", body);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representation.append(toCallString());
		representation.append(' ');
		body.represent(representation);
	}
}