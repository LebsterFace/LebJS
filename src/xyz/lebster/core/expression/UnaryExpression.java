package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.NotImplemented;

public record UnaryExpression(Expression expr, UnaryOp op) implements Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "UnaryExpression");
		Interpreter.dumpEnum(indent + 1, "UnaryOp", op.name());
		expr.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> oldValue = expr.execute(interpreter);
		return switch (op) {
			case Negate -> oldValue.toNumericLiteral().unaryMinus();
			case LogicalNot -> oldValue.toBooleanLiteral().not();
			default -> throw new NotImplemented("Unary operator '" + op.name() + "'");
		};
	}
}
