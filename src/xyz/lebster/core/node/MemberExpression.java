package xyz.lebster.core.node;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.exception.NotImplementedException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.NumericLiteral;
import xyz.lebster.core.value.Value;

public class MemberExpression extends Expression {
	public final Expression object;
	public final Identifier property;

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
