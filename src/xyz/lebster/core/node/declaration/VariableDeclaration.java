package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

public record VariableDeclaration(VariableDeclarator... declarations) implements Declaration {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "VariableDeclaration");
		for (VariableDeclarator declarator : declarations) {
			declarator.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		for (VariableDeclarator declarator : declarations) declarator.execute(interpreter);
		return Undefined.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (VariableDeclarator declaration : declarations) {
			declaration.represent(representation);
		}
	}
}