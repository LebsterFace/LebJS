package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

public record AssignmentExpression(LeftHandSideExpression left, Expression right, AssignmentOp op) implements Expression {
	public static final String invalidLHS = "Invalid left-hand side in assignment";

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Left", left)
			.operator(op)
			.child("Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-assignment-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);

		if (op == AssignmentOp.Assign) {
			final Value<?> right_value = right.execute(interpreter);
			left_reference.putValue(interpreter, right_value);
			return right_value;
		}

		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);
		final Value<?> result = BinaryExpression.applyOperator(interpreter, left_value, lookupBinaryOp(op), right_value);
		left_reference.putValue(interpreter, result);
		return result;
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(op.str);
		representation.append(' ');
		right.represent(representation);
	}

	private BinaryExpression.BinaryOp lookupBinaryOp(AssignmentOp op) {
		return switch (op) {
			case PlusAssign -> BinaryExpression.BinaryOp.Add;
			case MinusAssign -> BinaryExpression.BinaryOp.Subtract;
			case MultiplyAssign -> BinaryExpression.BinaryOp.Multiply;
			case DivideAssign -> BinaryExpression.BinaryOp.Divide;
			case ExponentAssign -> BinaryExpression.BinaryOp.Exponentiate;
			default -> throw new NotImplemented("BinaryOp pairing for " + op);
		};
	}

	public enum AssignmentOp {
		Assign("="),
		LogicalAndAssign("&&="),
		LogicalOrAssign("||="),
		NullishCoalesceAssign("??="),
		MultiplyAssign("*="),
		DivideAssign("/="),
		RemainderAssign("%="),
		PlusAssign("+="),
		MinusAssign("-="),
		LeftShiftAssign("<<="),
		RightShiftAssign(">>="),
		UnsignedRightShiftAssign(">>>="),
		BitwiseAndAssign("&="),
		BitwiseExclusiveOrAssign("^="),
		BitwiseOrAssign("|="),
		ExponentAssign("**=");

		private final String str;

		AssignmentOp(String s) {
			this.str = s;
		}
	}
}