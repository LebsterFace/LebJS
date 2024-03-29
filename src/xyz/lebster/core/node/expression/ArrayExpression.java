package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.array.ArrayObject;

public record ArrayExpression(SourceRange range, ExpressionList expressionList) implements Expression {
	@Override
	public ArrayObject execute(Interpreter interpreter) throws AbruptCompletion {
		return new ArrayObject(interpreter, expressionList.executeAll(interpreter));
	}
}