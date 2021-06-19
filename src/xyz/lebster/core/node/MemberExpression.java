package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageError;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;

public record MemberExpression(Expression object, Identifier property) implements Expression {

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("MemberExpression:");
		object.dump(indent + 1);
		property.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageError {
		final Dictionary obj = object.execute(interpreter).toDictionary();
		return obj.get(property);
	}
}
