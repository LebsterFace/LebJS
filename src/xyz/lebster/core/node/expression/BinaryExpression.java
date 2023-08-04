package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements Expression {
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

		if (left_num instanceof BigIntValue x && right_num instanceof final BigIntValue y) return switch (op) {
			case Add -> x.add(y);
			case BitwiseAND -> x.bitwiseAND(y);
			case BitwiseOR -> x.bitwiseOR(y);
			case BitwiseXOR -> x.bitwiseXOR(y);
			case Divide -> x.divide(interpreter, y);
			case Exponentiate -> x.exponentiate(interpreter, y);
			case LeftShift -> x.leftShift(y);
			case Multiply -> x.multiply(y);
			case Remainder -> x.remainder(interpreter, y);
			case SignedRightShift -> x.signedRightShift(y);
			case Subtract -> x.subtract(y);
			case UnsignedRightShift -> x.unsignedRightShift(interpreter, y);
		};

		if (left_num instanceof NumberValue x && right_num instanceof final NumberValue y) return switch (op) {
			case Add -> x.add(y);
			case BitwiseAND -> x.bitwiseAND(y);
			case BitwiseOR -> x.bitwiseOR(y);
			case BitwiseXOR -> x.bitwiseXOR(y);
			case Divide -> x.divide(y);
			case Exponentiate -> x.exponentiate(y);
			case LeftShift -> x.leftShift(y);
			case Multiply -> x.multiply(y);
			case Remainder -> x.remainder(y);
			case SignedRightShift -> x.signedRightShift(y);
			case Subtract -> x.subtract(y);
			case UnsignedRightShift -> x.unsignedRightShift(y);
		};

		throw new ShouldNotHappen("Attempting to mix BigInts and Numbers");
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(op.str);
		representation.append(' ');
		right.represent(representation);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatestringornumericbinaryexpression")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);
		return applyOperator(interpreter, left_value, op, right_value);
	}

	public enum BinaryOp {
		Add("+"),
		Subtract("-"),
		Multiply("*"),
		Divide("/"),
		Exponentiate("**"),
		Remainder("%"),
		LeftShift("<<"),
		SignedRightShift(">>"),
		UnsignedRightShift(">>>"),
		BitwiseAND("&"),
		BitwiseXOR("^"),
		BitwiseOR("|");

		private final String str;

		BinaryOp(String str) {
			this.str = str;
		}
	}
}