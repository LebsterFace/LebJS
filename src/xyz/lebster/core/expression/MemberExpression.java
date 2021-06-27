package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;


public record MemberExpression(Expression object, Expression property, boolean computed) implements Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "MemberExpression", "[computed=" + computed + "]");
		object.dump(indent + 1);
		property.dump(indent + 1);
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
