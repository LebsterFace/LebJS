package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

public record IdentifierExpression(String value) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return this.toReference(interpreter).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "IdentifierExpression", value);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getidentifierreference")
	public Reference toReference(Interpreter interpreter) {
		return interpreter.getBinding(new StringValue(value));
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value);
	}
}