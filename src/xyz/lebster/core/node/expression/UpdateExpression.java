package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.number.NumberValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-update-expressions")
public record UpdateExpression(LeftHandSideExpression expression, UpdateOp op) implements Expression {
	public static final String invalidPostLHS = "Invalid left-hand side expression in postfix operation";
	public static final String invalidPreLHS = "Invalid left-hand side expression in prefix operation";

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-postfix-increment-operator")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = expression.toReference(interpreter);
		final NumericValue<?> oldValue = left_reference.getValue(interpreter).toNumeric(interpreter);
		final NumericValue<?> newValue;
		if (oldValue instanceof final NumberValue N) newValue = switch (op) {
			case PostIncrement, PreIncrement -> N.add(NumberValue.ONE);
			case PostDecrement, PreDecrement -> N.subtract(NumberValue.ONE);
		};
		else if (oldValue instanceof final BigIntValue B) newValue = switch (op) {
			case PostIncrement, PreIncrement -> B.add(BigIntValue.ONE);
			case PostDecrement, PreDecrement -> B.subtract(BigIntValue.ONE);
		};
		else throw new ShouldNotHappen("Invalid numeric value");

		left_reference.putValue(interpreter, newValue);

		return switch (op) {
			case PostIncrement, PostDecrement -> oldValue;
			case PreIncrement, PreDecrement -> newValue;
		};
	}

	@Override
	public void represent(StringRepresentation representation) {
		switch (op) {
			case PreDecrement -> representation.append("--");
			case PreIncrement -> representation.append("++");
		}

		expression.represent(representation);

		switch (op) {
			case PostDecrement -> representation.append("--");
			case PostIncrement -> representation.append("++");
		}
	}

	public enum UpdateOp { PostIncrement, PostDecrement, PreIncrement, PreDecrement }
}
