package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.value.Number;
import xyz.lebster.core.value.Type;
import xyz.lebster.core.value.Value;

public class BinaryExpression extends Expression implements ASTNode {
	// FIXME: Values which are not Number should work!
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
		if (leftValue.type != Type.Number || rightValue.type != Type.Number) {
			throw new LanguageException("Currently, only numbers can be added!");
		}

		double lhs = ((Number) leftValue).value;
		double rhs = ((Number) rightValue).value;

		return new Number(switch (op) {
			case Add -> lhs + rhs;
			case Subtract -> lhs - rhs;
			case Divide -> lhs / rhs;
			case Multiply -> lhs * rhs;
		});
	}
}
