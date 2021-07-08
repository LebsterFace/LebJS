package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;

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
		final Dictionary executedBase = base.execute(interpreter).toDictionary(interpreter);
		final StringLiteral executedProp = (StringLiteral) (computed ? property.execute(interpreter) : property);
		return new Reference(executedBase, executedProp);
	}

	@Override
	public ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final Reference reference = toReference(interpreter);
		return new ExecutionContext(reference.getValue(interpreter), reference.base());
	}
}