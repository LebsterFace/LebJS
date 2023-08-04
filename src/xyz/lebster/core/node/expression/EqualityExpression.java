package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

public record EqualityExpression(SourceRange range, Expression left, Expression right, EqualityOp op) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-equality-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> x = left.execute(interpreter);
		final Value<?> y = right.execute(interpreter);

		return BooleanValue.of(switch (op) {
			case StrictEquals -> x.isStrictlyEqual(y);
			case StrictNotEquals -> !x.isStrictlyEqual(y);
			default -> throw new NotImplemented("EqualityOp: " + op);
		});
	}

	public enum EqualityOp {
		StrictEquals, StrictNotEquals,
		LooseEquals, LooseNotEquals
	}
}