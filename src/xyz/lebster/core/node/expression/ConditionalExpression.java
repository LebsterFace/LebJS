package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-conditional-operator")
public record ConditionalExpression(Expression test, Expression left, Expression right) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-conditional-operator-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final boolean result = test.execute(interpreter).isTruthy(interpreter);
		return (result ? left : right).execute(interpreter);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Test", test)
			.child("Left", left)
			.child("Right", right);
	}

	@Override
	public void represent(StringRepresentation representation) {
		test.represent(representation);
		representation.append(" ? ");
		left.represent(representation);
		representation.append(" : ");
		right.represent(representation);
	}
}