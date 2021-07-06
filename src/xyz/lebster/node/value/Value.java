package xyz.lebster.node.value;

import xyz.lebster.Dumper;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.expression.Expression;

import java.util.Objects;

public abstract class Value<JType> implements Expression {
	public final JType value;
	public final Type type;

	public Value(JType value, Type type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		return this;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, type.name(), toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public Primitive<?> toPrimitive(Interpreter interpreter, Type preferredType) {
		if (this instanceof Primitive) {
			return (Primitive<JType>) this;
		} else {
			throw new NotImplemented("A non-primitive value's toPrimitive method");
		}
	}

	public Primitive<?> toPrimitive(Interpreter interpreter) {
		return toPrimitive(interpreter, null);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tostring")
	public StringLiteral toStringLiteral(Interpreter interpreter) {
		return new StringLiteral(toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tonumber")
	public abstract NumericLiteral toNumericLiteral(Interpreter interpreter);

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toboolean")
	public abstract BooleanLiteral toBooleanLiteral(Interpreter interpreter);

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toobject")
	public abstract Dictionary toDictionary(Interpreter interpreter);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Value<?> value1)) return false;
		if (!Objects.equals(value, value1.value)) return false;
		return type == value1.type;
	}

	@Override
	public int hashCode() {
		int result = value != null ? value.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
