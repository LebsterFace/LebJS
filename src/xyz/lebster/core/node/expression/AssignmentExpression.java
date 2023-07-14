package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.node.declaration.IdentifierExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;

import static xyz.lebster.core.node.expression.AssignmentExpression.AssignmentOp.*;
import static xyz.lebster.core.node.expression.BinaryExpression.BinaryOp.*;
import static xyz.lebster.core.node.expression.LogicalExpression.LogicOp.*;
import static xyz.lebster.core.value.function.Executable.isAnonymousFunctionDefinition;

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
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-assignment-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (op == Assign) {
			if (left instanceof final AssignmentTarget dat) {
				return dat.assign(interpreter, right);
			} else {
				return left.assign(interpreter, right.execute(interpreter));
			}
		}

		if (!(left instanceof final LeftHandSideExpression lhs)) {
			throw new ShouldNotHappen("Invalid left-hand side in assignment");
		}

		// 1. Let lref be ? Evaluation of LeftHandSideExpression.
		final Reference left_reference = lhs.toReference(interpreter);
		// 2. Let lval be ? GetValue(lref).
		final Value<?> left_value = lhs.execute(interpreter);
		final Value<?> right_value;
		if (op == NullishCoalesceAssign || op == LogicalAndAssign || op == LogicalOrAssign) {
			switch (op) {
				case NullishCoalesceAssign -> {
					// 3. If lval is neither undefined nor null, return lval.
					if (!left_value.isNullish()) return left_value;
				}

				case LogicalOrAssign -> {
					// 3. Let lbool be ToBoolean(lval).
					final boolean left_boolean = left_value.isTruthy(interpreter);
					// 4. If lbool is true, return lval.
					if (left_boolean) return left_value;
				}

				case LogicalAndAssign -> {
					// 3. Let lbool be ToBoolean(lval).
					final boolean left_boolean = left_value.isTruthy(interpreter);
					// 4. If lbool is false, return lval.
					if (!left_boolean) return left_value;
				}
			}

			// 5. If IsAnonymousFunctionDefinition(AssignmentExpression) is true and IsIdentifierRef of LeftHandSideExpression is true, then
			if (isAnonymousFunctionDefinition(right) && left instanceof final IdentifierExpression identifierExpression) {
				// a. Let rval be ? NamedEvaluation of AssignmentExpression with argument lref.[[ReferencedName]].
				right_value = Executable.namedEvaluation(interpreter, right, left_reference.referencedName());
			}
			// 5. Else,
			else {
				// a. Let rref be ? Evaluation of AssignmentExpression.
				// b. Let rval be ? GetValue(rref).
				right_value = right.execute(interpreter);
			}

			// 6. Perform ? PutValue(lref, rval).
			left_reference.putValue(interpreter, right_value);
			// 7. Return rval.
			return right_value;
		}

		// 3. Let rref be ? Evaluation of AssignmentExpression.
		// 4. Let rval be ? GetValue(rref).
		right_value = right.execute(interpreter);
		// 5. Let assignmentOpText be the source text matched by AssignmentOperator.
		// 6. Let opText be the sequence of Unicode code points associated with assignmentOpText in the following table:
		// 7. Let r be ? ApplyStringOrNumericBinaryOperator(lval, opText, rval).
		final Value<?> result = AssignmentExpression.applyOperator(interpreter, left_value, op, right_value);
		// 8. Perform ? PutValue(lref, r).
		left_reference.putValue(interpreter, result);
		// 9. Return r.
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