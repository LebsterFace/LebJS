package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Type;
import xyz.lebster.core.node.value.Value;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements Expression {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-applystringornumericbinaryoperator")
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> left_value, BinaryOp op, Value<?> right_value) throws AbruptCompletion {
		if (op == BinaryOp.Add) {
			final Value<?> left_primitive = left_value.toPrimitive(interpreter);
			final Value<?> right_primitive = right_value.toPrimitive(interpreter);
			if (left_primitive.type == Type.String || right_primitive.type == Type.String) {
				final StringValue left_string = left_primitive.toStringLiteral(interpreter);
				final StringValue right_string = right_primitive.toStringLiteral(interpreter);
				return new StringValue(left_string.value + right_string.value);
			} else {
				left_value = left_primitive;
				right_value = right_primitive;
			}
		}

		final double left_num = left_value.toNumericLiteral(interpreter).value;
		final double right_num = right_value.toNumericLiteral(interpreter).value;

		return new NumberValue(switch (op) {
			case Add -> left_num + right_num;
			case Subtract -> left_num - right_num;
			case Multiply -> left_num * right_num;
			case Divide -> left_num / right_num;
			case Exponent -> Math.pow(left_num, right_num);
		});
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BinaryExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
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
		Exponent("**");

		public final String str;

		BinaryOp(String str) {
			this.str = str;
		}
	}
}