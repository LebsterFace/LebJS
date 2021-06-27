package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.Reference;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Value;

import java.util.Objects;

public class Identifier implements LeftHandSideExpression {
	public final String value;

	public Identifier(String name) {
		this.value = name;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpValue(indent, "Identifier", value);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return interpreter.getVariable(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "'" + value + "'";
	}

	public StringLiteral stringValue() {
		return new StringLiteral(value);
	}

	@Override
	public Reference toReference(Interpreter interpreter) {
		return new Reference(interpreter.thisValue().toDictionary(), stringValue());
	}
}