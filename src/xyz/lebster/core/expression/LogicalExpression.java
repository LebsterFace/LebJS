package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public class LogicalExpression implements Expression {
//	FIXME: implements BinaryExpression<LogicalOp>
	public final Expression left;
	public final Expression right;
	public final LogicalOp op;

	public LogicalExpression(Expression left, Expression right, LogicalOp op) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "LogicalExpression");
		left.dump(indent + 1);
		Interpreter.dumpEnum(indent + 1, "LogicalOp", op.name());
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> leftValue = left.execute(interpreter);
		final BooleanLiteral lbool = leftValue.toBooleanLiteral();
//		https://tc39.es/ecma262/#sec-binary-logical-operators-runtime-semantics-evaluation
//		FIXME: toPrimitive
		return switch (op) {
			case Or -> lbool.value ? leftValue : right.execute(interpreter);
			case And -> !lbool.value ? leftValue : right.execute(interpreter);
		};
	}
}
