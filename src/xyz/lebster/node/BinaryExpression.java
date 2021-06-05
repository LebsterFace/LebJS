package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.value.Number;
import xyz.lebster.value.Value;

public class BinaryExpression extends Expression implements ASTNode {
	// FIXME: Values which are not Number should work!
	public final Number left;
	public final Number right;
	public final BinaryOp op;

	public BinaryExpression(Number left, Number right, BinaryOp op) {
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
	public Value<?> execute(Interpreter interpreter) {
		double lhs = left.value.doubleValue();
		double rhs = right.value.doubleValue();

		return new Number(switch (op) {
			case Add -> lhs + rhs;
			case Subtract -> lhs - rhs;
			case Divide -> lhs / rhs;
			case Multiply -> lhs * rhs;
		});
	}
}
