package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

public record LogicalExpression(SourceRange range, Expression left, Expression right, LogicOp op) implements Expression {
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> left_value, LogicOp op, Value<?> right_value) throws AbruptCompletion {
		return switch (op) {
			case And -> left_value.isTruthy(interpreter) ? right_value : left_value;
			case Or -> left_value.isTruthy(interpreter) ? left_value : right_value;
			case Coalesce -> left_value.isNullish() ? right_value : left_value;
		};
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-binary-logical-operators")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> left_value = left.execute(interpreter);

		return switch (op) {
			case And -> left_value.isTruthy(interpreter) ? right.execute(interpreter) : left_value;
			case Or -> left_value.isTruthy(interpreter) ? left_value : right.execute(interpreter);
			case Coalesce -> left_value.isNullish() ? right.execute(interpreter) : left_value;
		};
	}

	public enum LogicOp {
		And, Or, Coalesce
	}
}