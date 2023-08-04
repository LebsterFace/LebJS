package xyz.lebster.core.node.statement;

import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public final class EmptyStatement implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return Undefined.instance;
	}

	@Override
	public SourceRange range() {
		throw new ShouldNotHappen("Attempting to get range() of EmptyStatement");
	}
}