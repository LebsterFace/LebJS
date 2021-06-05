package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

public class VariableDeclaration implements ASTNode {
	public VariableDeclarator[] declarations;

	public VariableDeclaration(VariableDeclarator[] declarations) {
		this.declarations = declarations;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("VariableDeclarator:");
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
