package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public class VariableDeclarator implements ASTNode {
	public final Identifier identifier;
	public final Expression init;

	public VariableDeclarator(Identifier identifier, Expression init) {
		this.identifier = identifier;
		this.init = init;
	}

	public VariableDeclarator(Identifier identifier) {
		this.identifier = identifier;
		this.init = new Undefined();
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("VariableDeclarator '");
		System.out.print(identifier.value);
		System.out.println("':");
		init.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		return interpreter.declareVariable(identifier, init.execute(interpreter));
	}
}
