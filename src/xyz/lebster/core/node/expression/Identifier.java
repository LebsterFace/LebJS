package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.Value;

public record Identifier(String value) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return interpreter.getReference(this).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", value);
	}

	@Override
	public Reference toReference(Interpreter interpreter) {
		return interpreter.getReference(this);
	}

	public StringValue stringValue() {
		return new StringValue(toString());
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(value);
	}
}