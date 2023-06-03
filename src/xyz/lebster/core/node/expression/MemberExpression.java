package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.Kind;
import xyz.lebster.core.node.expression.literal.StringLiteral;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.Key;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record MemberExpression(Expression base, Expression property, boolean computed) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return toReference(interpreter).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.selfParameterized(this, computed ? "Computed" : "Non-Computed")
			.child("Base", base)
			.child("ReferencedName", property);
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
	public void represent(StringRepresentation representation) {
		base.represent(representation);
		if (computed) {
			representation.append('[');
			property.represent(representation);
			representation.append(']');
		} else {
			representation.append('.');
			representation.append(((StringLiteral) property).value().value);
		}
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> value) throws AbruptCompletion {
		throw new ShouldNotHappen("Cannot declare MemberExpression");
	}
}