package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.Reference;
import xyz.lebster.core.runtime.ReferenceError;
import xyz.lebster.core.value.Value;

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
		if (left instanceof LeftHandSideExpression) {
			var lhs = (LeftHandSideExpression) left;
			final Reference ref = lhs.toReference(interpreter);
			final Value<?> rhs = right.execute(interpreter);
			interpreter.setVariable(ref, rhs);
			return rhs;
		} else {
			return interpreter.throwValue(new ReferenceError("Invalid left-hand side in assignment: " + left.getClass().getSimpleName()));
		}
	}
}
