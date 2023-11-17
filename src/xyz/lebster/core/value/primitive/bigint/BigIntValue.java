package xyz.lebster.core.value.primitive.bigint;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.parser.Lexer;
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
	protected String displayColor() {
		return ANSI.BRIGHT_CYAN;
	}

	@Override
	protected String rawDisplayString() {
		return value.toString() + "n";
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

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-numeric-types-bigint-leftShift")
	public static BigInteger leftShift(BigInteger x, BigInteger y) {
		// 1. If y < 0ℤ, then
		if (y.compareTo(BigInteger.ZERO) < 0) {
			// a. Return ℤ(floor(ℝ(x) / 2^(-ℝ(y)))).
			return x.divide(BigInteger.TWO.pow(-y.intValueExact()));
		}

		// 2. Return x × 2ℤ^y.
		return x.multiply(BigInteger.TWO.pow(y.intValueExact()));
	}
}
