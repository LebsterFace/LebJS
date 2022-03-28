package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

import static xyz.lebster.core.runtime.value.primitive.NumberValue.UINT32_LIMIT;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements Expression {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-applystringornumericbinaryoperator")
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> left_value, BinaryOp op, Value<?> right_value) throws AbruptCompletion {
		if (op == BinaryOp.Add) {
			final Value<?> left_primitive = left_value.toPrimitive(interpreter);
			final Value<?> right_primitive = right_value.toPrimitive(interpreter);
			if (left_primitive instanceof StringValue || right_primitive instanceof StringValue) {
				final StringValue left_string = left_primitive.toStringValue(interpreter);
				final StringValue right_string = right_primitive.toStringValue(interpreter);
				return new StringValue(left_string.value + right_string.value);
			} else {
				left_value = left_primitive;
				right_value = right_primitive;
			}
		}

		final NumberValue left_number = left_value.toNumberValue(interpreter);
		final NumberValue right_number = right_value.toNumberValue(interpreter);
		final double LNV = left_number.value;
		final double RNV = right_number.value;

		return new NumberValue(switch (op) {
			case Add -> LNV + RNV;
			case Subtract -> LNV - RNV;
			case Multiply -> LNV * RNV;
			case Divide -> LNV / RNV;
			case Exponentiate -> Math.pow(LNV, RNV);
			case Remainder -> LNV % RNV;

			// https://tc39.es/ecma262/multipage#sec-numeric-types-number-leftShift
			case LeftShift -> (double) (left_number.toInt32() << (right_number.toUint32() % 32));
			case SignedRightShift -> (double) (left_number.toInt32() >> (right_number.toUint32() % 32));
			case UnsignedRightShift -> {
				// (NaN|0|+-Infinity) >>> x = 0
				if (Double.isNaN(LNV) || LNV == 0.0 || Double.isInfinite(LNV))
					yield 0D;

				final int left_int32 = (int) ((long) LNV % NumberValue.TWO_TO_THE_32);

				/// x >>> (NaN|0|+-Infinity) = uint32(x)
				if (Double.isNaN(RNV) || RNV == 0.0 || Double.isInfinite(RNV))
					yield (double) (left_int32 & UINT32_LIMIT);

				final long right_uint32 = (long) RNV % NumberValue.TWO_TO_THE_32;
				final long shiftCount = right_uint32 % 32;
				yield (double) ((left_int32 >>> shiftCount) & UINT32_LIMIT);
			}

			// https://tc39.es/ecma262/multipage#sec-numberbitwiseop
			case BitwiseAND -> (double) (left_number.toInt32() & right_number.toInt32());
			case BitwiseXOR -> (double) (left_number.toInt32() ^ right_number.toInt32());
			case BitwiseOR -> (double) (left_number.toInt32() | right_number.toInt32());
		});
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Left", left)
			.operator(op)
			.child("Right", right);
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