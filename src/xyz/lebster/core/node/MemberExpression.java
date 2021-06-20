package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageError;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

// FIXME: Support computed properties
public final class MemberExpression extends Expression {
	private final Expression object;
	private final Identifier property;

	public MemberExpression(Expression object, Identifier property) {
		this.object = object;
		this.property = property;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("MemberExpression:");
		object.dump(indent + 1);
		property.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final Dictionary obj = object.execute(interpreter).toDictionary();
		return obj.get(property);
	}
}
