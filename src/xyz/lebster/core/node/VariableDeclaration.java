package xyz.lebster.core.node;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;


public record VariableDeclaration(VariableDeclarator... declarations) implements Declaration {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "VariableDeclaration");
		for (VariableDeclarator declarator : declarations) {
			declarator.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		for (VariableDeclarator declarator : declarations) {
			declarator.execute(interpreter);
		}

		return new Undefined();
	}
}
