package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.NumericLiteral;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Type;
import xyz.lebster.node.value.Value;

public record BinaryExpression(Expression left, Expression right, BinaryOp op) implements Expression {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-applystringornumericbinaryoperator")
	public static Value<?> applyOperator(Interpreter interpreter, Value<?> lval, BinaryOp op, Value<?> rval) {
		if (op == BinaryOp.Add) {
			final Value<?> lprim = lval.toPrimitive();
			final Value<?> rprim = rval.toPrimitive();
			if (lprim.type == Type.String || rprim.type == Type.String) {
				final StringLiteral lstr = lprim.toStringLiteral();
				final StringLiteral rstr = rprim.toStringLiteral();
				return new StringLiteral(lstr.value + rstr.value);
			} else {
				lval = lprim;
				rval = rprim;
			}
		}

		final NumericLiteral lnum = lval.toNumericLiteral();
		final NumericLiteral rnum = rval.toNumericLiteral();
		final double result = switch (op) {
			case Add -> lnum.value + rnum.value;
			case Subtract -> lnum.value - rnum.value;
			case Multiply -> lnum.value * rnum.value;
			case Divide -> lnum.value / rnum.value;
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
		representation.append(switch (op) {
			case Add -> '+';
			case Subtract -> '-';
			case Multiply -> '*';
			case Divide -> '/';
		});
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
		Add, Subtract, Multiply, Divide
	}
}