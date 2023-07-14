package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

public record SequenceExpression(Expression left, Expression right) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-comma-operator-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let lref be the result of evaluating left.
		// 2. Perform ? GetValue(lref).
		left.execute(interpreter);
		// 3. Let rref be the result of evaluating right.
		// 4. Return ? GetValue(rref).
		return right.execute(interpreter);
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(',');
		right.represent(representation);
	}
}