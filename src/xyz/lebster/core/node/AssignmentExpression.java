package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.exception.NotImplemented;

public final class AssignmentExpression extends Expression {
	private final Expression left;
	private final Expression right;
	private final AssignmentOp op;

	public AssignmentExpression(Expression left, Expression right, AssignmentOp op) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("AssignmentExpression:");
		left.dump(indent + 1);
		op.dump(indent + 2);
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		if (left instanceof Identifier) {
			final Value<?> rhs = right.execute(interpreter);
			interpreter.setVariable((Identifier) left, rhs);
			return rhs;
		} else {
			throw new NotImplemented("AssignmentExpression with '" + left.getClass().getSimpleName() + "' as left-hand side");
		}
	}
}
