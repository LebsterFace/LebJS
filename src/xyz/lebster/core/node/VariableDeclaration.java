package xyz.lebster.core.node;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public record VariableDeclaration(VariableDeclarator[] declarations) implements ASTNode {
	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("VariableDeclaration:");
		for (VariableDeclarator declarator : declarations) declarator.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		for (VariableDeclarator declarator : declarations) {
			declarator.execute(interpreter);
		}

		return new Undefined();
	}
}
