package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;

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
		return new Undefined();
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (VariableDeclarator declaration : declarations) {
			declaration.represent(representation);
		}
	}
}