package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record ReturnStatement(SourceRange range, Expression value) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> valueToReturn = value == null ? Undefined.instance : value.execute(interpreter);
		throw new AbruptCompletion(valueToReturn, AbruptCompletion.Type.Return);
	}
}