package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;

public record ParenthesizedExpression(Expression expression, SourceRange range) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return expression.execute(interpreter);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Expression", expression);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('(');
		expression.represent(representation);
		representation.append(')');
	}
}