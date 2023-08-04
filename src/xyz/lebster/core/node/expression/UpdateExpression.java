package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

import java.math.BigInteger;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-update-expressions")
public record UpdateExpression(SourceRange range, LeftHandSideExpression expression, UpdateOp op) implements Expression {
	public static final String invalidPostLHS = "Invalid left-hand side expression in postfix operation";
	public static final String invalidPreLHS = "Invalid left-hand side expression in prefix operation";

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-postfix-increment-operator")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = expression.toReference(interpreter);
		final NumericValue<?> oldValue = left_reference.getValue(interpreter).toNumeric(interpreter);
		final NumericValue<?> newValue = applyOperator(oldValue);
		left_reference.putValue(interpreter, newValue);
		return switch (op) {
			case PostIncrement, PostDecrement -> oldValue;
			case PreIncrement, PreDecrement -> newValue;
		};
	}

	private NumericValue<?> applyOperator(NumericValue<?> oldValue) {
		if (oldValue instanceof final NumberValue N) return new NumberValue(switch (op) {
			case PostIncrement, PreIncrement -> N.value + 1;
			case PostDecrement, PreDecrement -> N.value - 1;
		});

		if (oldValue instanceof final BigIntValue B) return new BigIntValue(switch (op) {
			case PostIncrement, PreIncrement -> B.value.add(BigInteger.ONE);
			case PostDecrement, PreDecrement -> B.value.subtract(BigInteger.ONE);
		});

		throw new ShouldNotHappen("Invalid numeric value");
	}

	public enum UpdateOp { PostIncrement, PostDecrement, PreIncrement, PreDecrement }
}
