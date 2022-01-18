package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;

public record AssignmentExpression(LeftHandSideExpression left, Expression right, AssignmentOp op) implements Expression {
	public static final String invalidLHS = "Invalid left-hand side in assignment";

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "AssignmentExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-assignment-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);

		return switch (op) {
			case Assign -> {
				final Value<?> right_value = right.execute(interpreter);
				left_reference.setValue(interpreter, right_value);
				yield right_value;
			}

			default -> {
				final Value<?> left_value = left.execute(interpreter);
				final Value<?> right_value = right.execute(interpreter);
				final Value<?> result = BinaryExpression.applyOperator(interpreter, left_value, lookupBinaryOp(op), right_value);
				left_reference.setValue(interpreter, result);
				yield result;
			}
		};
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(op == AssignmentOp.Assign ? "" : lookupBinaryOp(op).str);
		representation.append("= ");
		right.represent(representation);
	}

	private BinaryExpression.BinaryOp lookupBinaryOp(AssignmentOp op) {
		return switch (op) {
			case PlusAssign -> BinaryExpression.BinaryOp.Add;
			case MinusAssign -> BinaryExpression.BinaryOp.Subtract;
			case MultiplyAssign -> BinaryExpression.BinaryOp.Multiply;
			case DivideAssign -> BinaryExpression.BinaryOp.Divide;
			case ExponentAssign -> BinaryExpression.BinaryOp.Exponent;
			case Assign -> throw new NotImplemented("BinaryOp for AssignmentOp.Assign");
		};
	}

	public enum AssignmentOp {
		Assign,
		PlusAssign,
		MultiplyAssign,
		DivideAssign,
		MinusAssign,
		ExponentAssign
	}
}