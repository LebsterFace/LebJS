package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.Reference;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Value;


public record MemberExpression(Expression object, Expression property, boolean computed) implements LeftHandSideExpression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "MemberExpression", computed ? "Computed" : "NonComputed");
		Interpreter.dumpIndicated(indent + 1, "BaseObject", object);
		Interpreter.dumpIndicated(indent + 1, "ReferencedName", property);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference reference = toReference(interpreter);
		return reference.getValue(interpreter);
	}

	@Override
	public CallFrame toCallFrame(Interpreter interpreter) throws AbruptCompletion {
		final Reference reference = toReference(interpreter);
		return new CallFrame(reference.getValue(interpreter), reference.baseObj());
	}

	@Override
	public Reference toReference(Interpreter interpreter) throws AbruptCompletion {
		final Dictionary obj = object.execute(interpreter).toDictionary();
		final StringLiteral prop = computed ? property.execute(interpreter).toStringLiteral() : (StringLiteral) property;
		return new Reference(obj, prop);
	}
}
