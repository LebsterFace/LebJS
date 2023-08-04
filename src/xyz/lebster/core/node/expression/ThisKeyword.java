package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

public record ThisKeyword(SourceRange range) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.thisValue();
	}
}