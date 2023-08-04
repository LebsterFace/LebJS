package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record IfStatement(SourceRange range, Expression condition, Statement consequence, Statement elseStatement) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (condition.execute(interpreter).isTruthy(interpreter)) {
			return consequence.execute(interpreter);
		} else if (elseStatement == null) {
			return Undefined.instance;
		} else {
			return elseStatement.execute(interpreter);
		}
	}
}