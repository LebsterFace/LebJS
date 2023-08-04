package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-conditional-operator")
public record ConditionalExpression(SourceRange range, Expression test, Expression left, Expression right) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-conditional-operator-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final boolean result = test.execute(interpreter).isTruthy(interpreter);
		return (result ? left : right).execute(interpreter);
	}
}