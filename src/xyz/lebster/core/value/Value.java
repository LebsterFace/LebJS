package xyz.lebster.core.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.globals.Null;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.math.BigDecimal;
import java.util.Objects;

import static xyz.lebster.core.value.primitive.bigint.BigIntValue.stringToBigInt;

public abstract class Value<JType> implements Displayable {
	public final JType value;

	public Value(JType value) {
		this.value = value;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-islessthan")
	public static BooleanValue isLessThan(Interpreter interpreter, Value<?> x, Value<?> y, boolean leftFirst) throws AbruptCompletion {
		// 1. If LeftFirst is true, then
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


		// 3. If px is a String and py is a String, then
		if (px instanceof final StringValue string_px && py instanceof final StringValue string_py) {
			// a. Let lx be the length of px.
			final int lx = string_px.value.length();
			// b. Let ly be the length of py.
			final int ly = string_py.value.length();
			// c. For each integer i such that 0 ≤ i < min(lx, ly), in ascending order, do
			for (int i = 0; i < Math.min(lx, ly); i++) {
				// i. Let cx be the numeric value of the code unit at index i within px.
				final int cx = string_px.value.charAt(i);
				// ii. Let cy be the numeric value of the code unit at index i within py.
				final int cy = string_py.value.charAt(i);
				// iii. If cx < cy, return true.
				if (cx < cy) return BooleanValue.TRUE;
				// iv. If cx > cy, return false.
				if (cx > cy) return BooleanValue.FALSE;
			}

			// d. If lx < ly, return true. Otherwise, return false.
			return BooleanValue.of(lx < ly);
		}
		// 4. Else,
		else {
			// a. If px is a BigInt and py is a String, then
			if (px instanceof final BigIntValue bigint_px && py instanceof final StringValue string_py) {
				// i. Let ny be StringToBigInt(py).
				final BigIntValue ny = stringToBigInt(string_py.value);
				// ii. If ny is undefined, return undefined.
				if (ny == null) return null;
				// iii. Return BigInt::lessThan(px, ny).
				return bigint_px.lessThan(ny);
			}
			// b. If px is a String and py is a BigInt, then
			if (px instanceof final StringValue string_px && py instanceof final BigIntValue bigint_py) {
				// i. Let nx be StringToBigInt(px).
				final BigIntValue nx = stringToBigInt(string_px.value);
				// ii. If nx is undefined, return undefined.
				if (nx == null) return null;
				// iii. Return BigInt::lessThan(nx, py).
				return nx.lessThan(bigint_py);
			}
			// c. NOTE: Because px and py are primitive values, evaluation order is not important.
			// d. Let nx be ? ToNumeric(px).
			final NumericValue<?> nx = px.toNumeric(interpreter);
			// e. Let ny be ? ToNumeric(py).
			final NumericValue<?> ny = py.toNumeric(interpreter);
			// f. If Type(nx) is Type(ny), then
			if (nx.sameType(ny)) {
				// i. If nx is a Number, then
				if (nx instanceof final NumberValue N) {
					// 1. Return Number::lessThan(nx, ny).
					return N.lessThan((NumberValue) ny);
				}
				// ii. Else,
				else {
					// 1. Assert: nx is a BigInt.
					if (!(nx instanceof final BigIntValue B)) throw new ShouldNotHappen("Invalid numeric value");
					// 2. Return BigInt::lessThan(nx, ny).
					return B.lessThan((BigIntValue) ny);
				}
			}
			// g. Assert: nx is a BigInt and ny is a Number, or nx is a Number and ny is a BigInt.
			if (!(nx instanceof BigIntValue && ny instanceof NumberValue || nx instanceof NumberValue && ny instanceof BigIntValue))
				throw new ShouldNotHappen("Assertion failed");
			// h. If nx or ny is NaN, return undefined.
			if ((nx instanceof final NumberValue X && X.value.isNaN()) || (ny instanceof final NumberValue Y && Y.value.isNaN())) return null;
			// i. If nx is -∞𝔽 or ny is +∞𝔽, return true.
			if ((nx instanceof final NumberValue X && X.value < 0 && X.value.isInfinite()) || (ny instanceof final NumberValue Y && Y.value > 0 && Y.value.isInfinite())) return BooleanValue.TRUE;
			// j. If nx is +∞𝔽 or ny is -∞𝔽, return false.
			if ((nx instanceof final NumberValue X && X.value > 0 && X.value.isInfinite()) || (ny instanceof final NumberValue Y && Y.value < 0 && Y.value.isInfinite())) return BooleanValue.FALSE;
			// k. If ℝ(nx) < ℝ(ny), return true; otherwise return false.
			final BigDecimal rNX = nx instanceof final NumberValue X ? new BigDecimal(X.value) : new BigDecimal(((BigIntValue) nx).value);
			final BigDecimal rNY = ny instanceof final NumberValue Y ? new BigDecimal(Y.value) : new BigDecimal(((BigIntValue) ny).value);
			return BooleanValue.of(rNX.compareTo(rNY) < 0);
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
	public NumericValue<?> toNumeric(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let primValue be ? ToPrimitive(value, number).
		final PrimitiveValue<?> primValue = toPrimitive(interpreter, PreferredType.Number);
		// 2. If primValue is a BigInt, return primValue.
		if (primValue instanceof final BigIntValue B) return B;
		// 3. Return ? ToNumber(primValue).
		return primValue.toNumberValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevaluezero")
	public boolean sameValueZero(Value<?> y) {
		// 7.2.11 SameValueZero ( x, y )

		// 1. If Type(x) is not Type(y), return false.
		if (!sameType(y)) return false;
		// 2. If x is a Number, then
		if (this instanceof final NumberValue x)
			// a. Return Number::sameValueZero(x, y).
			return x.isEqualTo((NumberValue) y, true, true);
		// 3. Return SameValueNonNumber(x, y).
		return sameValueNonNumber(y);
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
		// 1. If Type(x) is not Type(y), return false.
		if (!this.sameType(y)) return false;
		// 2. If x is a Number, then
		if (this instanceof final NumberValue x)
			// a. Return Number::sameValue(x, y).
			return x.isEqualTo((NumberValue) y, false, true);
		// 3. Return SameValueNonNumber(x, y).
		return sameValueNonNumber(y);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isstrictlyequal")
	public final boolean isStrictlyEqual(Value<?> y) {
		// 1. If Type(x) is not Type(y), return false.
		if (!this.sameType(y)) return false;
		// 2. If x is a Number, then
		if (this instanceof final NumberValue x)
			// a. Return Number::equal(x, y).
			return x.isEqualTo((NumberValue) y, true, false);
		// 3. Return SameValueNonNumber(x, y).
		return sameValueNonNumber(y);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-samevaluenonnumber")
	public final boolean sameValueNonNumber(Value<?> y) {
		// 1. Assert: Type(x) is Type(y).
		assert sameType(y);
		// 2. If x is either null or undefined, return true.
		if (isNullish()) return true;
		// TODO: 3. If x is a BigInt, then Return BigInt::equal(x, y).
		// 4. If x is a String, then If x and y have the same length and the same code units in the same positions,
		// return true; otherwise, return false.
		// 5. If x is a Boolean, then If x and y are both true or both false,
		// return true; otherwise, return false.
		// 6. NOTE: All other ECMAScript language values are compared by identity.
		// 7. If x is y, return true; otherwise, return false.
		return equals(y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return Objects.equals(value, ((Value<?>) o).value);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#table-typeof-operator-results")
	public abstract String typeOf();

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