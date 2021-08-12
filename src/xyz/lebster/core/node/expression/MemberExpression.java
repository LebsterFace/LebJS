package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Type;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.TypeError;

public record MemberExpression(Expression base, Expression property, boolean computed) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return toReference(interpreter).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "MemberExpression", computed ? "Computed" : "NonComputed");
		Dumper.dumpIndicated(indent + 1, "Base", base);
		Dumper.dumpIndicated(indent + 1, "ReferencedName", property);
	}

	@Override
	public Reference toReference(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> executedBase = base.execute(interpreter);
		final StringLiteral executedProp = property.execute(interpreter).toStringLiteral(interpreter);

		if (executedBase.type == Type.Undefined || executedBase.type == Type.Null) {
			final String msg = "Cannot read property '" +
							   executedProp.value + "' of " +
							   (executedBase.type == Type.Undefined ?
								   "undefined" : "null");

			throw AbruptCompletion.error(new TypeError(msg));
		}

		return new Reference(executedBase.toDictionary(interpreter), executedProp);
	}

	@Override
	public ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final Reference reference = toReference(interpreter);
		return new ExecutionContext(interpreter.lexicalEnvironment(), reference.getValue(interpreter), reference.base());
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
			representation.append(((StringLiteral) property).value);
		}
	}
}