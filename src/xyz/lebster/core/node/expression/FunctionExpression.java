package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.primitive.string.StringValue;

public record FunctionExpression(BlockStatement body, StringLiteral name, FunctionParameters parameters) implements FunctionNode, Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		final StringValue executedName = name == null ? Names.EMPTY : name.execute(interpreter);
		return new Function(interpreter.intrinsics, executedName, interpreter.environment(), this);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representCall(representation);
		representation.append(' ');
		body.represent(representation);
	}
}