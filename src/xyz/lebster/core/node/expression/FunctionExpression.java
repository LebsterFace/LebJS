package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionArguments;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Function;

public record FunctionExpression(BlockStatement body, String name, FunctionArguments arguments) implements FunctionNode, Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Function(interpreter, interpreter.environment(), this);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representation.append(toCallString());
		representation.append(' ');
		body.represent(representation);
	}
}