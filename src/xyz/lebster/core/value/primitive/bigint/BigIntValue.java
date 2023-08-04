package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigInteger;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-language-types-bigint-type")
public final class BigIntValue extends NumericValue<BigInteger> {
	public static final BigIntValue ONE = new BigIntValue(BigInteger.ONE);
	public static final BigIntValue ZERO = new BigIntValue(BigInteger.ZERO);

	public BigIntValue(BigInteger value) {
		super(value);
	}

	public BigIntValue(String value) {
		super(new BigInteger(value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-stringtobigint")
	public static BigIntValue stringToBigInt(String str) {
		// 1. Let text be StringToCodePoints(str).
		// 2. Let literal be ParseText(text, StringIntegerLiteral).
		// 3. If literal is a List of errors, return undefined.
		// TODO: Implement better whitespace logic
		str = str.toLowerCase().trim();
		if (str.isEmpty()) return BigIntValue.ZERO;
		final StringBuilder builder = new StringBuilder();

		final int start;
		final int radix;
		if (str.startsWith("0x")) {
			radix = 16;
			start = 2;
		} else if (str.startsWith("0b")) {
			radix = 2;
			start = 2;
		} else if (str.startsWith("0o")) {
			radix = 8;
			start = 2;
		} else {
			radix = 10;
			if (str.startsWith("-") || str.startsWith("+")) {
				start = 1;
				builder.append(str.charAt(0));
			} else {
				start = 0;
			}
		}

		for (int i = start; i < str.length(); i++) {
			if (Lexer.isDigit(str.charAt(i), radix)) {
				builder.append(str.charAt(i));
			} else {
				return null;
			}
		}

		if (builder.isEmpty()) return null;

		// 4. Let mv be the MV of literal.
		final BigInteger mv = new BigInteger(builder.toString(), radix);
		// 5. Assert: mv is an integer.
		// 6. Return ℤ(mv).
		return new BigIntValue(mv);
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append(value.toString());
		representation.append('n');
		representation.append(ANSI.RESET);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-tostring")
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		return new StringValue(value.toString());
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert a BigInt value to a number"));
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return BooleanValue.of(value.compareTo(BigInteger.ZERO) == 0);
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
		return new BigIntWrapper(interpreter.intrinsics, this);
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) {
		return this;
	}

	@Override
	public String typeOf() {
		return "bigint";
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-add")
	public BigIntValue add(BigIntValue y) {
		// 1. Return x + y.
		return new BigIntValue(value.add(y.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-bitwiseAND")
	public BigIntValue bitwiseAND(BigIntValue other) {
		throw new NotImplemented("BigInt#bitwiseAND");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-bitwiseOR")
	public BigIntValue bitwiseOR(BigIntValue other) {
		throw new NotImplemented("BigIntbitwiseOR#");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-bitwiseXOR")
	public BigIntValue bitwiseXOR(BigIntValue other) {
		throw new NotImplemented("BigInt#bitwiseXOR");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-divide")
	public BigIntValue divide(Interpreter interpreter, BigIntValue y) throws AbruptCompletion {
		// 1. If y is 0ℤ, throw a RangeError exception.
		if (y.value.compareTo(BigInteger.ZERO) == 0) throw error(new RangeError(interpreter, "Division by zero"));
		// 2. Let quotient be ℝ(x) / ℝ(y).
		final BigInteger quotient = value.divide(y.value);
		// 3. Return ℤ(truncate(quotient)).
		return new BigIntValue(quotient);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-exponentiate")
	public BigIntValue exponentiate(Interpreter interpreter, BigIntValue exponent) throws AbruptCompletion {
		// 1. If exponent < 0ℤ, throw a RangeError exception.
		if (exponent.value.compareTo(BigInteger.ZERO) < 0) throw error(new RangeError(interpreter, "BigInt negative exponent"));
		// 2. If base is 0ℤ and exponent is 0ℤ, return 1ℤ.
		if (value.compareTo(BigInteger.ZERO) == 0 && exponent.value.compareTo(BigInteger.ZERO) == 0) return ONE;
		// 3. Return base raised to the power exponent.
		return new BigIntValue(value.pow(exponent.value.intValueExact()));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-leftShift")
	private static BigInteger leftShift(BigInteger x, BigInteger y) {
		// 1. If y < 0ℤ, then
		if (y.compareTo(BigInteger.ZERO) < 0) {
			// a. Return ℤ(floor(ℝ(x) / 2^(-ℝ(y)))).
			return x.divide(BigInteger.TWO.pow(-y.intValueExact()));
		}

		// 2. Return x × 2ℤ^y.
		return x.multiply(BigInteger.TWO.pow(y.intValueExact()));
	}

	public BigIntValue leftShift(BigIntValue y) {
		return new BigIntValue(leftShift(value, y.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-multiply")
	public BigIntValue multiply(BigIntValue other) {
		// 1. Return x × y.
		return new BigIntValue(value.multiply(other.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-remainder")
	public BigIntValue remainder(Interpreter interpreter, BigIntValue d) throws AbruptCompletion {
		// 1. If d is 0ℤ, throw a RangeError exception.
		if (d.value.compareTo(BigInteger.ZERO) == 0) throw error(new RangeError(interpreter, "Division by zero"));
		// 2. If n is 0ℤ, return 0ℤ.
		if (value.compareTo(BigInteger.ZERO) == 0) return ZERO;
		// 3. Let quotient be ℝ(n) / ℝ(d).
		// 4. Let q be ℤ(truncate(quotient)).
		// 5. Return n - (d × q).
		return new BigIntValue(value.remainder(d.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-signedRightShift")
	public BigIntValue signedRightShift(BigIntValue y) {
		// 1. Return BigInt::leftShift(x, -y).
		return new BigIntValue(leftShift(value, y.value.negate()));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-subtract")
	public BigIntValue subtract(BigIntValue y) {
		// 1. Return x - y.
		return new BigIntValue(value.subtract(y.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-unsignedRightShift")
	public BigIntValue unsignedRightShift(Interpreter interpreter, BigIntValue other) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "BigInts have no unsigned right shift, use >> instead"));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-bitwiseNOT")
	public BigIntValue bitwiseNOT() {
		// 1. Return -x - 1ℤ.
		return new BigIntValue(value.negate().subtract(BigInteger.ONE));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-unaryMinus")
	public BigIntValue unaryMinus() {
		// 1. If x is 0ℤ, return 0ℤ.
		// 2. Return -x.
		return new BigIntValue(value.negate());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-lessThan")
	public BooleanValue lessThan(BigIntValue y) {
		// 1. If ℝ(x) < ℝ(y), return true; otherwise return false.
		return BooleanValue.of(value.compareTo(y.value) < 0);
	}
}
