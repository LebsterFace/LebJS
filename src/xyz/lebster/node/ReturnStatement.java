package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

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
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> value = argument.execute(interpreter);
		interpreter.doReturn(value);
		return value;
	}
}
