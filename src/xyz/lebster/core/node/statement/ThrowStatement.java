package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record ThrowStatement(SourceRange range, Expression value) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw error(value.execute(interpreter));
	}
}