package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.value.Value;

import static xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp.Assign;
import static xyz.lebster.core.node.expression.BinaryExpression.BinaryOp.*;
import static xyz.lebster.core.node.expression.LogicalExpression.LogicOp.*;

public record AssignmentExpression(Assignable left, Expression right, AssignmentOp op) implements Expression {
	public static final String invalidLHS = "Invalid left-hand side in assignment";

	private static Value<?> applyOperator(Interpreter interpreter, Value<?> left_value, AssignmentOp op, Value<?> right_value) throws AbruptCompletion {
		return switch (op) {
			case Assign -> throw new ShouldNotHappen("AssignmentExpression#applyOperator called on AssignmentOp.Assign");

			case LogicalAndAssign, LogicalOrAssign, NullishCoalesceAssign -> LogicalExpression.applyOperator(interpreter, left_value, lookupLogicOp(op), right_value);

			default -> BinaryExpression.applyOperator(interpreter, left_value, lookupBinaryOp(op), right_value);
		};
	}

	private static BinaryExpression.BinaryOp lookupBinaryOp(AssignmentOp op) {
		return switch (op) {
			case BitwiseAndAssign -> BitwiseAND;
			case BitwiseExclusiveOrAssign -> BitwiseXOR;
			case BitwiseOrAssign -> BitwiseOR;
			case DivideAssign -> Divide;
			case ExponentAssign -> Exponentiate;
			case LeftShiftAssign -> LeftShift;
			case MinusAssign -> Subtract;
			case MultiplyAssign -> Multiply;
			case PlusAssign -> Add;
			case RemainderAssign -> Remainder;
			case RightShiftAssign -> SignedRightShift;
			case UnsignedRightShiftAssign -> UnsignedRightShift;
			default -> throw new ShouldNotHappen("AssignmentExpression#lookupBinaryOp called on " + op.name());
		};
	}

	private static LogicalExpression.LogicOp lookupLogicOp(AssignmentOp op) {
		return switch (op) {
			case LogicalAndAssign -> And;
			case LogicalOrAssign -> Or;
			case NullishCoalesceAssign -> Coalesce;
			default -> throw new ShouldNotHappen("AssignmentExpression#lookupLogicOp called on " + op.name());
		};
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
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-assignment-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (op == Assign) {
			if (left instanceof final AssignmentTarget dat) {
				return dat.assign(interpreter, right);
			} else {
				return left.assign(interpreter, right.execute(interpreter));
			}
		}

		if (!(left instanceof final LeftHandSideExpression lhs))
			throw new ShouldNotHappen("Invalid left-hand side in assignment");

		final Reference left_reference = lhs.toReference(interpreter);
		final Value<?> left_value = lhs.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);
		final Value<?> result = AssignmentExpression.applyOperator(interpreter, left_value, op, right_value);
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