package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;

import java.util.Objects;

public abstract class Value<JType> implements Expression {
	public final JType value;
	public final Type type;

	public Value(JType value, Type type) {
		this.value = value;
		this.type = type;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return this;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, type.name(), String.valueOf(value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public Primitive<?> toPrimitive(Interpreter interpreter, Type preferredType) throws AbruptCompletion {
		if (this instanceof Primitive) {
			return (Primitive<JType>) this;
		} else {
			throw new NotImplemented("A non-primitive value's toPrimitive method");
		}
	}

	public Primitive<?> toPrimitive(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, null);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tostring")
	public abstract StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tonumber")
	public abstract NumericLiteral toNumericLiteral(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toboolean")
	public abstract BooleanLiteral toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toobject")
	public abstract Dictionary toDictionary(Interpreter interpreter) throws AbruptCompletion;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Value<?> value1)) return false;
		if (!Objects.equals(value, value1.value)) return false;
		return type == value1.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, type);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#table-typeof-operator-results")
	public abstract String typeOf(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-topropertykey")
	public Dictionary.Key<?> toPropertyKey(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let key be ? ToPrimitive(argument, string).
		final var key = this.toPrimitive(interpreter, Type.String);
		// 2. If Type(key) is Symbol, then
		if (key instanceof final Symbol s)
			// a. Return key.
			return s;

		// 3. Return ! ToString(key).
		return key.toStringLiteral(interpreter);
	}

	public boolean isTruthy(Interpreter interpreter) throws AbruptCompletion {
		return this.toBooleanLiteral(interpreter).value;
	}

	public boolean isNullish() {
		return type == Type.Undefined || type == Type.Null;
	}
}