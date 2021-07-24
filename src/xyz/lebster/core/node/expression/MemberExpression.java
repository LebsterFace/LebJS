package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;

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
		final Dictionary executedBase = base.execute(interpreter).toDictionary();
		final StringLiteral executedProp = property.execute(interpreter).toStringLiteral();
		return new Reference(executedBase, executedProp);
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