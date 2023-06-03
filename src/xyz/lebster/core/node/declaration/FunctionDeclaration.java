package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.node.declaration.Kind.Let;

public record FunctionDeclaration(BlockStatement body, StringLiteral name, FunctionParameters parameters) implements FunctionNode, Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final StringValue executedName = name == null ? Names.EMPTY : name.execute(interpreter);
		final Function function = new Function(interpreter.intrinsics, executedName, interpreter.environment(), this);
		interpreter.declareVariable(Let, executedName, function);
		return Undefined.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representCall(representation);
		representation.append(' ');
		body.represent(representation);
	}
}