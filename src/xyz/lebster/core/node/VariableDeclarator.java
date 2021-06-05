package xyz.lebster.core.node;

import xyz.lebster.core.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public class VariableDeclarator implements ASTNode {
	public final Identifier name;
	public final Value<?> init;

	public VariableDeclarator(Identifier name, Value<?> init) {
		this.name = name;
		this.init = init;
	}

	public VariableDeclarator(Identifier name) {
		this.name = name;
		this.init = new Undefined();
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("VariableDeclarator '");
		System.out.print(name.value);
		System.out.println("':");
		init.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		return interpreter.declareVariable(name, init);
	}
}
