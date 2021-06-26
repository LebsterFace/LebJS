package xyz.lebster.core.expression;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Type;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements ASTNode, Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "BinaryExpression");
		left.dump(indent + 1);
		Interpreter.dumpEnum(indent + 1, "BinaryOp", op.name());
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> leftValue = left.execute(interpreter);
		final Value<?> rightValue = right.execute(interpreter);
//		https://tc39.es/ecma262/#sec-applystringornumericbinaryoperator
//		FIXME: toPrimitive
		if (op == BinaryOp.Add && (leftValue.type == Type.String || rightValue.type == Type.String)) {
			final String lstr = leftValue.toStringLiteral().value;
			final String rstr = rightValue.toStringLiteral().value;
			return new StringLiteral(lstr + rstr);
		}

		final double lnum = leftValue.toNumericLiteral().value;
		final double rnum = rightValue.toNumericLiteral().value;

		return new NumericLiteral(switch (op) {
			case Add -> lnum + rnum;
			case Subtract -> lnum - rnum;
			case Divide -> lnum / rnum;
			case Multiply -> lnum * rnum;
		});
	}
}
