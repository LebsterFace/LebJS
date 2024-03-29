package xyz.lebster.core.node.expression;

import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.declaration.Kind;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.Key;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record MemberExpression(SourceRange range, Expression base, Expression property, boolean computed) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return toReference(interpreter).getValue(interpreter);
	}

	@Override
	public Reference toReference(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> executedBase = base.execute(interpreter);
		final Key<?> executedProp = property.execute(interpreter).toPropertyKey(interpreter);

		if (executedBase.isNullish()) {
			final String msg = "Cannot read property '" + executedProp.value + "' of " + executedBase;
			throw error(new TypeError(interpreter, msg));
		}

		return new Reference(executedBase.toObjectValue(interpreter), executedProp);
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> value) {
		throw new ShouldNotHappen("Cannot declare MemberExpression");
	}
}