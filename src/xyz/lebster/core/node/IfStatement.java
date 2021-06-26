package xyz.lebster.core.node;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public record IfStatement(Expression condition, Statement consequence, Statement elseStatement) implements Statement {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "IfStatement");
		condition.dump(indent + 1);
		consequence.dump(indent + 1);
		elseStatement.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final BooleanLiteral res = condition.execute(interpreter).toBooleanLiteral();
		if (res.value) {
			return consequence.execute(interpreter);
		} else if (elseStatement == null) {
			return new Undefined();
		} else {
			return elseStatement.execute(interpreter);
		}
	}
}
