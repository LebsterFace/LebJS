package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.IdentifierExpression;
import xyz.lebster.core.value.Value;

public final class ThisKeyword implements Expression {
	private static final IdentifierExpression _IDENTIFIER_EXPRESSION =
		new IdentifierExpression("this");

	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.thisValue();
	}

	public void dump(int indent) {
		_IDENTIFIER_EXPRESSION.dump(indent);
	}

	@Override
	public void represent(StringRepresentation representation) {
		_IDENTIFIER_EXPRESSION.represent(representation);
	}
}