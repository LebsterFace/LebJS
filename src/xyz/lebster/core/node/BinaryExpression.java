package xyz.lebster.core.node;

import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Type;
import xyz.lebster.core.value.Value;

public class BinaryExpression extends Expression implements ASTNode {
	public final Expression left;
	public final Expression right;
	public final BinaryOp op;

	public BinaryExpression(Expression left, Expression right, BinaryOp op) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("BinaryExpression:");
		left.dump(indent + 1);
		op.dump(indent + 2);
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> leftValue = left.execute(interpreter);
		final Value<?> rightValue = right.execute(interpreter);

		if (leftValue.type == Type.Number && rightValue.type == Type.Number) {
			final double lhs = ((NumericLiteral) leftValue).value;
			final double rhs = ((NumericLiteral) rightValue).value;

			return new NumericLiteral(switch (op) {
				case Add -> lhs + rhs;
				case Subtract -> lhs - rhs;
				case Divide -> lhs / rhs;
				case Multiply -> lhs * rhs;
			});
		} else if (op == BinaryOp.Add && leftValue.type == Type.String && rightValue.type == Type.String) {
			final String lhs = ((StringLiteral) leftValue).value;
			final String rhs = ((StringLiteral) rightValue).value;

			return new StringLiteral(lhs + rhs);
		} else {
			throw new NotImplementedException("BinaryExpression (" +
				leftValue.type + ", " +
				op + ", " +
				rightValue.type +
			")");
		}
	}
}
