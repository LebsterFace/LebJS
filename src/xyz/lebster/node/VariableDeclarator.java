package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

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
