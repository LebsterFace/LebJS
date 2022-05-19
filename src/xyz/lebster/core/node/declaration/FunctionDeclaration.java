package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Function;
import xyz.lebster.core.value.globals.Undefined;

public record FunctionDeclaration(BlockStatement body, String name, String[] arguments) implements FunctionNode, Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Function function = new Function(interpreter, interpreter.lexicalEnvironment(), this);
		interpreter.declareVariable(name, function);
		return Undefined.instance;
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