package xyz.lebster.core.node.expression.literal;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.regexp.RegExpObject;

public record RegExpLiteral(String pattern) implements Expression {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent).value(this, pattern);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(pattern);
	}

	public RegExpObject execute(Interpreter interpreter) {
		return new RegExpObject(interpreter.intrinsics.regExpPrototype, pattern);
	}
}
