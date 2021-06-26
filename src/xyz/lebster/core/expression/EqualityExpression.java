package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;
import xyz.lebster.exception.LanguageException;

public class EqualityExpression extends Expression {
	private final Expression left;
	private final Expression right;
	private final EqualityOp op;

	public EqualityExpression(Expression left, Expression right, EqualityOp op) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "EqualityExpression");
		left.dump(indent + 1);
		Interpreter.dumpEnum(indent + 1, "EqualityOp", op.name());
		right.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Value<?> leftValue = left.execute(interpreter);
		final Value<?> rightValue = right.execute(interpreter);

		return new BooleanLiteral(switch (op) {
			case StrictEquals -> isStrictlyEqual(leftValue, rightValue);
			case StrictNotEquals -> !isStrictlyEqual(leftValue, rightValue);
		});
	}

	private static boolean isStrictlyEqual(Value<?> x, Value<?> y) {
//		https://tc39.es/ecma262/#sec-isstrictlyequal
		if (x.type != y.type) return false;
//		https://tc39.es/ecma262/#sec-samevaluenonnumeric
		return switch (x.type) {
			case Undefined, Null -> true;
			case Number -> (double) x.value == (double) y.value;
			case Boolean -> (boolean) x.value == (boolean) y.value;
			case String, Dictionary -> x.value.equals(y.value);
			default -> false;
		};
	}
}
