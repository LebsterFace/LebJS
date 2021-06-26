package xyz.lebster.core.value;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.prototype.ObjectPrototype;

import java.util.Objects;

abstract public class Value<JType> extends Expression {
	public final Type type;
	public final JType value;

	public Value(Type type, JType value) {
		this.type = type;
		this.value = value;
	}

	public StringLiteral toStringLiteral() {
		return new StringLiteral(toString());
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public abstract BooleanLiteral toBooleanLiteral();

	public abstract NumericLiteral toNumericLiteral();

	public abstract Function toFunction();

	public abstract Dictionary toDictionary();

	public Identifier toIdentifier() {
		return new Identifier(toStringLiteral().value);
	}

	public Dictionary getPrototype() {
		return ObjectPrototype.instance;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print(type);
		System.out.print(": ");
		System.out.println(value);
	}

	@Override
	public Value<JType> execute(Interpreter interpreter) {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Value<?> other)) return false;

		return type == other.type && (
			value == other.value ||
			(value != null && other.value != null && value.equals(other.value))
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}
}
