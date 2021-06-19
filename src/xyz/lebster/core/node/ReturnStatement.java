package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageError;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public class ReturnStatement implements ASTNode {
	public final Expression argument;

	public ReturnStatement(Expression argument) {
		this.argument = argument;
	}

	public ReturnStatement() {
		this.argument = new Undefined();
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("ReturnStatement:");
		argument.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageError {
		return interpreter.doExit(argument.execute(interpreter));
	}
}
