package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.regexp.RegExpObject;

public record RegExpLiteral(SourceRange range, String pattern, String flags) implements Expression {
	public RegExpObject execute(Interpreter interpreter) throws AbruptCompletion {
		return new RegExpObject(interpreter, pattern, flags);
	}
}
