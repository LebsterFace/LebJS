package xyz.lebster.core.node;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;


public record IfStatement(Expression condition, Statement consequence, Statement elseStatement) implements Statement {
	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "IfStatement");
		Interpreter.dumpIndicated(indent + 1, "Condition", condition);
		Interpreter.dumpIndicated(indent + 1, "Consequence", consequence);
		if (elseStatement != null) {
			Interpreter.dumpIndicated(indent + 1, "ElseStatement", elseStatement);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
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
