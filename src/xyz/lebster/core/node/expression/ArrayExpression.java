package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.array.ArrayObject;

public record ArrayExpression(SourceRange range, ExpressionList expressionList) implements Expression {
	@Override
	public ArrayObject execute(Interpreter interpreter) throws AbruptCompletion {
		return new ArrayObject(interpreter, expressionList.executeAll(interpreter));
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.container(expressionList);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('[');
		expressionList.represent(representation);
		representation.append(']');
	}
}