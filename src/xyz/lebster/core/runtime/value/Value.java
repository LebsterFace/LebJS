package xyz.lebster.core.runtime.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.*;

import java.util.Objects;

public abstract class Value<JType> {
	public final JType value;
	public final Type type;

	public Value(JType value, Type type) {
		this.value = value;
		this.type = type;
	}

	public abstract void display(StringRepresentation representation);

	public void displayForConsoleLog(StringRepresentation representation) {
		this.display(representation);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public PrimitiveValue<?> toPrimitive(Interpreter interpreter, Type preferredType) throws AbruptCompletion {
		if (this instanceof PrimitiveValue) {
			return (PrimitiveValue<JType>) this;
		} else {
			throw new NotImplemented("A non-primitive value's toPrimitive method");
		}
	}

	public PrimitiveValue<?> toPrimitive(Interpreter interpreter) throws AbruptCompletion {
		return toPrimitive(interpreter, null);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tostring")
	public abstract StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tonumber")
	public abstract NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toboolean")
	public abstract BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toobject")
	public abstract ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion;

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevalue")
	public boolean sameValue(Value<?> y) {
		// 1. If Type(x) is different from Type(y), return false.
		if (this.type != y.type) return false;

		// 2. If Type(x) is Number, then
		if (this instanceof final NumberValue x_n)
			// a. Return ! Number::sameValue(x, y).
			return x_n.sameValue((NumberValue) y);

		// FIXME: BigInt
		// 3. If Type(x) is BigInt, then
		// a. Return ! BigInt::sameValue(x, y).

		// 4. Return ! SameValueNonNumeric(x, y).
		return this.sameValueNonNumeric(y);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevaluenonnumeric")
	public boolean sameValueNonNumeric(Value<?> y) {
		// 1. Assert: Type(x) is the same as Type(y).
		// 2. If Type(x) is Undefined, return true.
		if (this == UndefinedValue.instance) return true;
		// 3. If Type(x) is Null, return true.
		if (this == NullValue.instance) return true;

		if (this.type == Value.Type.String)
			// 4. If Type(x) is String, then
			return this.value.equals(y.value);
		// a. If x and y are exactly the same sequence of code units (same length and same code units at corresponding indices), return true; otherwise, return false.

		// 5. If Type(x) is Boolean, then
		// a. If x and y are both true or both false, return true; otherwise, return false.
		// 6. If Type(x) is Symbol, then
		// a. If x and y are both the same Symbol value, return true; otherwise, return false.
		// 7. If x and y are the same Object value, return true. Otherwise, return false.

		return this == y;
	}

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
	public ObjectValue.Key<?> toPropertyKey(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let key be ? ToPrimitive(argument, string).
		final var key = this.toPrimitive(interpreter, Value.Type.String);
		// 2. If Type(key) is Symbol, then
		if (key instanceof final SymbolValue s)
			// a. Return key.
			return s;

		// 3. Return ! ToString(key).
		return key.toStringValue(interpreter);
	}

	public boolean isTruthy(Interpreter interpreter) throws AbruptCompletion {
		return this.toBooleanValue(interpreter).value;
	}

	public boolean isNullish() {
		return type == Type.Undefined || type == Type.Null;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getmethod")
	public Value<?> getMethod(Interpreter interpreter, ObjectValue.Key<?> P) throws AbruptCompletion {
		// 1. Let func be ? GetV(V, P).
		final var func = this.getV(interpreter, P);
		// 2. If func is either undefined or null, return undefined.
		if (func == UndefinedValue.instance || func == NullValue.instance)
			return UndefinedValue.instance;
		// 3. If IsCallable(func) is false, throw a TypeError exception.
		if (!(func instanceof Executable<?>))
			throw AbruptCompletion.error(new TypeError("Not a function!"));
		// 4. Return func.
		return func;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getv")
	private Value<?> getV(Interpreter interpreter, ObjectValue.Key<?> P) throws AbruptCompletion {
		// 1. Let O be ? ToObject(V).
		final ObjectValue O = this.toObjectValue(interpreter);
		// 2. Return ? O.[[Get]](P, V).
		return O.get(interpreter, P);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-primitive-value")
	public enum Type {
		String,
		Symbol,
		// TODO: BigInt
		Number,
		Boolean,
		Object,
		Null,
		Undefined
	}
}