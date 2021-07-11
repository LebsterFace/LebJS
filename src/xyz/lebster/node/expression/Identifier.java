package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;

public record Identifier(String value) implements LeftHandSideExpression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return toReference(interpreter).getValue(interpreter);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", value);
	}

	@Override
	public Reference toReference(Interpreter interpreter) {
		return interpreter.getReference(this);
	}

	public StringLiteral stringValue() {
		return new StringLiteral(toString());
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