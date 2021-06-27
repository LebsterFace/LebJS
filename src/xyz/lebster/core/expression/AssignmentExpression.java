package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.NotImplemented;

public record AssignmentExpression(Expression left, Expression right, AssignmentOp op) implements Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "AssignmentExpression");
		left.dump(indent + 1);
		Interpreter.dumpEnum(indent, "AssignmentOp", op.name());
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (left instanceof Identifier) {
			final Value<?> rhs = right.execute(interpreter);
			interpreter.setVariable((Identifier) left, rhs);
			return rhs;
		} else {
			throw new NotImplemented("AssignmentExpression with '" + left.getClass().getSimpleName() + "' as left-hand side");
		}
	}
}
