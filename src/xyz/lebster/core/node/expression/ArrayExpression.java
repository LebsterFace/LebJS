package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.object.ArrayObject;

public record ArrayExpression(ExpressionList expressionList) implements Expression {
	@Override
	public ArrayObject execute(Interpreter interpreter) throws AbruptCompletion {
		return new ArrayObject(expressionList.executeAll(interpreter));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ArrayExpression");
		expressionList.dump(indent + 1);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('[');
		expressionList.represent(representation);
		representation.append(']');
	}
}