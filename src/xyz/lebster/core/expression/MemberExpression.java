package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;


public record MemberExpression(Expression object, Expression property, boolean computed) implements Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "MemberExpression", computed ? "Computed" : "NonComputed");
		Interpreter.dumpIndicated(indent + 1, "BaseObject", object);
		Interpreter.dumpIndicated(indent + 1, "ReferencedName", property);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
//		TODO: Copied from toCallFrame, can we remove?
		final Dictionary obj = object.execute(interpreter).toDictionary();
		final Identifier prop = computed ? property.execute(interpreter).toIdentifier() : (Identifier) property;
		return obj.get(prop);
	}

	@Override
	public CallFrame toCallFrame(Interpreter interpreter) throws AbruptCompletion {
		final Dictionary obj = object.execute(interpreter).toDictionary();
		final Identifier prop = computed ? property.execute(interpreter).toIdentifier() : (Identifier) property;
		return new CallFrame(obj.get(prop), obj);
	}
}
