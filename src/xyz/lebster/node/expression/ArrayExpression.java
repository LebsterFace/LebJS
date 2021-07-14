package xyz.lebster.node.expression;

import xyz.lebster.ANSI;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ArrayObject;

import java.util.ArrayList;
import java.util.List;

public record ArrayExpression(List<Expression> elements) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final List<Value<?>> results = new ArrayList<>(elements.size());
		for (Expression element : elements) results.add(element.execute(interpreter));
		return new ArrayObject(results);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BACKGROUND_BRIGHT_YELLOW);
		representation.append("(ArrayExpression)");
		representation.append(ANSI.RESET);
	}

	@Override
	public void dump(int indent) {

	}
}