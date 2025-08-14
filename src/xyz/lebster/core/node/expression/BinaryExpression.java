package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static java.math.BigInteger.ZERO;
import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record BinaryExpression(SourceRange range, Expression left, Expression right, BinaryOp op) implements Expression {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-applystringornumericbinaryoperator")
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> left_value, BinaryOp op, Value<?> right_value) throws AbruptCompletion {
		// 1. If opText is +, then
		if (op == BinaryOp.Add) {
			// a. Let left_primitive be ? ToPrimitive(left_value).
			final Value<?> left_primitive = left_value.toPrimitive(interpreter);
			// b. Let right_primitive be ? ToPrimitive(right_value).
			final Value<?> right_primitive = right_value.toPrimitive(interpreter);
			// c. If left_primitive is a String or right_primitive is a String, then
			if (left_primitive instanceof StringValue || right_primitive instanceof StringValue) {
				// i. Let left_string be ? ToString(left_primitive).
				final StringValue left_string = left_primitive.toStringValue(interpreter);
				// ii. Let right_string be ? ToString(right_primitive).
				final StringValue right_string = right_primitive.toStringValue(interpreter);
				// iii. Return the string-concatenation of left_string and right_string.
				return new StringValue(left_string.value + right_string.value);
			} else {
				// d. Set left_value to left_primitive.
				left_value = left_primitive;
				// e. Set right_value to right_primitive.
				right_value = right_primitive;
			}
		}

		// 2. NOTE: At this point, it must be a numeric operation.
		// 3. Let left_num be ? ToNumeric(left_value).
		final NumericValue<?> left_num = left_value.toNumeric(interpreter);
		// 4. Let right_num be ? ToNumeric(right_value).
		final NumericValue<?> right_num = right_value.toNumeric(interpreter);
		// 5. If Type(left_num) is not Type(right_num), throw a TypeError exception.
		if (left_num.getClass() != right_num.getClass())
			throw error(new TypeError(interpreter, "Cannot mix BigInt and other types, use explicit conversions"));

		if (left_num instanceof BigIntValue x && right_num instanceof final BigIntValue y) return new BigIntValue(switch (op) {
			case Add -> x.value.add(y.value);
			case BitwiseAND -> x.value.and(y.value);
			case BitwiseOR -> x.value.or(y.value);
			case BitwiseXOR -> x.value.xor(y.value);
			case Multiply -> x.value.multiply(y.value);
			case Subtract -> x.value.subtract(y.value);

			case LeftShift -> BigIntValue.leftShift(x.value, y.value);
			case SignedRightShift -> BigIntValue.leftShift(x.value, y.value.negate());
			case UnsignedRightShift -> throw error(new TypeError(interpreter, "BigInts have no unsigned right shift, use >> instead"));

			case Divide -> {
				if (y.value.compareTo(ZERO) == 0) throw error(new RangeError(interpreter, "Division by zero"));
				yield x.value.divide(y.value);
			}

			case Exponentiate -> {
				if (y.value.compareTo(ZERO) < 0) throw error(new RangeError(interpreter, "BigInt negative exponent"));
				yield x.value.pow(y.value.intValueExact());
			}

			case Remainder -> {
				if (y.value.compareTo(ZERO) == 0) throw error(new RangeError(interpreter, "Division by zero"));
				yield x.value.remainder(y.value);
			}
		});

		if (left_num instanceof NumberValue x && right_num instanceof final NumberValue y) return new NumberValue(switch (op) {
			case Add -> x.value + y.value;
			case Divide -> x.value / y.value;
			case Exponentiate -> Math.pow(x.value, y.value);
			case Multiply -> x.value * y.value;
			case Remainder -> x.value % y.value;
			case Subtract -> x.value - y.value;

			case BitwiseAND -> (double) (x.toInt32() & y.toInt32());
			case BitwiseOR -> (double) (x.toInt32() | y.toInt32());
			case BitwiseXOR -> (double) (x.toInt32() ^ y.toInt32());
			case LeftShift -> (double) (x.toInt32() << (y.toUint32() % 32));
			case SignedRightShift -> (double) (x.toInt32() >> (y.toUint32() % 32));
			case UnsignedRightShift -> (double) (x.toUint32() >>> (y.toUint32() % 32));
		});

		throw new ShouldNotHappen("Attempting to mix BigInts and Numbers");
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatestringornumericbinaryexpression")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);
		return applyOperator(interpreter, left_value, op, right_value);
	}

	public enum BinaryOp {
		Add, Subtract, Multiply, Divide, Exponentiate, Remainder,
		LeftShift, SignedRightShift, UnsignedRightShift,
		BitwiseAND, BitwiseXOR, BitwiseOR
	}
}