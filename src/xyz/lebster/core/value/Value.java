package xyz.lebster.core.value;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.Objects;

public abstract class Value<JType> implements Displayable {
	public final JType value;

	public Value(JType value) {
		this.value = value;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-islessthan")
	public static BooleanValue isLessThan(Interpreter interpreter, Value<?> x, Value<?> y, boolean leftFirst) throws AbruptCompletion {
		// 1. If the LeftFirst flag is true, then
		PrimitiveValue<?> px;
		PrimitiveValue<?> py;

		if (leftFirst) {
			// a. Let px be ? ToPrimitive(x, number).
			px = x.toPrimitive(interpreter, Value.PreferredType.Number);
			// b. Let py be ? ToPrimitive(y, number).
			py = y.toPrimitive(interpreter, Value.PreferredType.Number);
		}
		// 2. Else,
		else {
			// a. NOTE: The order of evaluation needs to be reversed to preserve left to right evaluation.
			// b. Let py be ? ToPrimitive(y, number).
			py = y.toPrimitive(interpreter, Value.PreferredType.Number);
			// c. Let px be ? ToPrimitive(x, number).
			px = x.toPrimitive(interpreter, Value.PreferredType.Number);
		}


		// 3. If Type(px) is String and Type(py) is String, then
		if (px instanceof final StringValue string_px && py instanceof final StringValue string_py) {

			// a. If IsStringPrefix(py, px) is true, return false.
			if (isStringPrefix(string_py.value, string_px.value)) return BooleanValue.FALSE;
			// b. If IsStringPrefix(px, py) is true, return true.
			if (isStringPrefix(string_px.value, string_py.value)) return BooleanValue.TRUE;

			// c. Let k be the smallest non-negative integer such that the code unit at index k
			//    within px is different from the code unit at index k within py.
			//    (There must be such a k, for neither String is a prefix of the other.)
			int k = 0;
			while (k < string_px.value.length()) {
				if (string_px.value.charAt(k) != string_py.value.charAt(k)) {
					break;
				}

				k++;
			}

			// d. Let m be the integer that is the numeric value of the code unit at index k within px.
			int m = string_px.value.charAt(k);
			// e. Let n be the integer that is the numeric value of the code unit at index k within py.
			int n = string_py.value.charAt(k);
			// f. If m < n, return true. Otherwise, return false.
			return BooleanValue.of(m < n);
		}
		// 4. Else,
		else {
			// FIXME: BigInt for this entire block

			// c. NOTE: Because px and py are primitive values, evaluation order is not important.
			// d. Let nx be ? ToNumeric(px).
			final NumberValue nx = px.toNumeric(interpreter);
			// e. Let ny be ? ToNumeric(py).
			final NumberValue ny = py.toNumeric(interpreter);

			// 1. Return Number::lessThan(nx, ny).
			return nx.lessThan(ny);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isstringprefix")
	private static boolean isStringPrefix(String p, String q) {
		// 1. If ! StringIndexOf(q, p, 0) is 0, return true.
		// 2. Else, return false.
		return q.indexOf(p) == 0;
	}

	public void displayForConsoleLog(StringRepresentation representation) {
		this.display(representation);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-toprimitive")
	public PrimitiveValue<?> toPrimitive(Interpreter interpreter, PreferredType preferredType) throws AbruptCompletion {
		if (this instanceof PrimitiveValue) {
			return (PrimitiveValue<JType>) this;
		} else {
			throw new NotImplemented(this.getClass().getSimpleName() + "#toPrimitive");
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tonumeric")
	@NonCompliant
	public NumberValue toNumeric(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let primValue be ? ToPrimitive(value, number).
		final PrimitiveValue<?> primValue = toPrimitive(interpreter, PreferredType.Number);
		// TODO: 2. If primValue is a BigInt, return primValue.
		// 3. Return ? ToNumber(primValue).
		return primValue.toNumberValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevaluezero")
	public boolean sameValueZero(Value<?> y) {
		// 7.2.12 SameValueZero ( x, y )
		// 1. If Type(x) is different from Type(y), return false.
		if (!sameType(y)) return false;
		// 2. If Type(x) is Number, then
		if (this instanceof final NumberValue x) {
			// a. Return Number::sameValueZero(x, y).
			return NumberValue.sameValueZero(x, (NumberValue) y);
		}
		// TODO: 3. If Type(x) is BigInt, then
		//           a. Return BigInt::sameValueZero(x, y).

		// 4. Return SameValueNonNumeric(x, y).
		return sameValueNonNumeric(y);
	}

	public final boolean sameType(Value<?> y) {
		if (this instanceof ObjectValue) {
			return y instanceof ObjectValue;
		} else {
			return this.getClass() == y.getClass();
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevalue")
	public final boolean sameValue(Value<?> y) {
		// 1. If Type(x) is different from Type(y), return false.
		if (!this.sameType(y)) return false;

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
		if (this == Undefined.instance) return true;
		// 3. If Type(x) is Null, return true.
		if (this == Null.instance) return true;

		if (this instanceof StringValue)
			// 4. If Type(x) is String, then
			// a. If x and y are exactly the same sequence of code units
			//    (same length and same code units at corresponding indices),
			//    return true; otherwise, return false.
			return this.value.equals(y.value);

		// 5. If Type(x) is Boolean, then
		// a. return (x and y are both true or both false)
		// 6. If Type(x) is Symbol, then
		// a. return (x and y are both the same Symbol value)
		// 7. If x and y are the same Object value, return true. Otherwise, return false.

		return this == y;
	}

	@Override
	@NonCompliant
	public boolean equals(Object o) {
		// FIXME: 0 === -0 should be true
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return Objects.equals(value, ((Value<?>) o).value);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#table-typeof-operator-results")
	public abstract String typeOf(Interpreter interpreter);

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-topropertykey")
	public final Key<?> toPropertyKey(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let key be ? ToPrimitive(argument, string).
		final PrimitiveValue<?> key = this.toPrimitive(interpreter, PreferredType.String);
		// 2. If Type(key) is Symbol, then
		if (key instanceof final SymbolValue s)
			// a. Return key.
			return s;

		// 3. Return ! ToString(key).
		return key.toStringValue(interpreter);
	}

	public final boolean isTruthy(Interpreter interpreter) throws AbruptCompletion {
		return this.toBooleanValue(interpreter).value;
	}

	public final boolean isNullish() {
		return this == Undefined.instance || this == Null.instance;
	}

	public enum PreferredType { String, Number }
}