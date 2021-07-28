package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SpecificationURL;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Type;
import xyz.lebster.core.node.value.Value;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements Expression {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-applystringornumericbinaryoperator")
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> lval, BinaryOp op, Value<?> rval) throws AbruptCompletion {
		if (op == BinaryOp.Add) {
			final Value<?> lprim = lval.toPrimitive(interpreter);
			final Value<?> rprim = rval.toPrimitive(interpreter);
			if (lprim.type == Type.String || rprim.type == Type.String) {
				final StringLiteral lstr = lprim.toStringLiteral(interpreter);
				final StringLiteral rstr = rprim.toStringLiteral(interpreter);
				return new StringLiteral(lstr.value + rstr.value);
			} else {
				lval = lprim;
				rval = rprim;
			}
		}

		final NumericLiteral lnum = lval.toNumericLiteral(interpreter);
		final NumericLiteral rnum = rval.toNumericLiteral(interpreter);
		final double result = switch (op) {
			case Add -> lnum.value + rnum.value;
			case Subtract -> lnum.value - rnum.value;
			case Multiply -> lnum.value * rnum.value;
			case Divide -> lnum.value / rnum.value;
			case Exponent -> Math.pow(lnum.value, rnum.value);
		};

		return new NumericLiteral(result);
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
		final Value<?> lval = left.execute(interpreter);
		final Value<?> rval = right.execute(interpreter);
		return applyOperator(interpreter, lval, op, rval);
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