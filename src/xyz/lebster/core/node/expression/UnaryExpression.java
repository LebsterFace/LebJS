package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-unary-operators")
public record UnaryExpression(Expression expression, UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case UnaryPlus -> expression.execute(interpreter).toNumberValue(interpreter);
			case UnaryMinus -> expression.execute(interpreter).toNumberValue(interpreter).unaryMinus();
			case LogicalNot -> expression.execute(interpreter).toBooleanValue(interpreter).not();
			case Typeof -> typeofOperator(interpreter);
			case Void -> voidOperator(interpreter);
			case Delete -> deleteOperator(interpreter);
			case BitwiseNot -> bitwiseNotOperator(interpreter);

			case Await -> throw new NotImplemented("The `await` operator");
		};
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bitwise-not-operator")
	private Value<?> bitwiseNotOperator(Interpreter interpreter) throws AbruptCompletion {
		//  1. Let expr be ? Evaluation of UnaryExpression.
		final Value<?> expr = expression.execute(interpreter);
		//  2. Let oldValue be ? ToNumeric(? GetValue(expr)).
		final NumberValue oldValue = expr.toNumeric(interpreter);
		//  3. If oldValue is a Number, then return Number::bitwiseNOT(oldValue).
		return oldValue.bitwiseNOT();
		//  TODO: 4. Else, Assert: oldValue is a BigInt.
		//  b. Return BigInt::bitwiseNOT(oldValue).
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-typeof-operator")
	private StringValue typeofOperator(Interpreter interpreter) throws AbruptCompletion {
		if (expression instanceof final LeftHandSideExpression lhs) {
			final Reference reference = lhs.toReference(interpreter);
			if (reference.isResolvable()) {
				return new StringValue(reference.getValue(interpreter).typeOf(interpreter));
			} else {
				return Names.undefined;
			}
		} else {
			return new StringValue(expression.execute(interpreter).typeOf(interpreter));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-void-operator")
	private Undefined voidOperator(Interpreter interpreter) throws AbruptCompletion {
		// UnaryExpression : void UnaryExpression
		// 1. Let expr be the result of evaluating UnaryExpression.
		// 2. Perform ? GetValue(expr).
		expression.execute(interpreter);
		// 3. Return undefined.
		return Undefined.instance;
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-delete-operator")
	private BooleanValue deleteOperator(Interpreter interpreter) throws AbruptCompletion {
		if (!(expression instanceof final MemberExpression memberExpression)) {
			return BooleanValue.TRUE;
		}

		final ObjectValue obj = memberExpression.base().execute(interpreter).toObjectValue(interpreter);
		final Value<?> propertyName = memberExpression.property().execute(interpreter);
		return BooleanValue.of(obj.delete(propertyName.toPropertyKey(interpreter)));
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Expression", expression)
			.operator(op);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(switch (op) {
			case UnaryMinus -> '-';
			case LogicalNot -> '!';
			case UnaryPlus -> '+';
			case Delete -> "delete ";
			case Void -> "void ";
			case Typeof -> "typeof ";
			case BitwiseNot -> '~';
			case Await -> "await ";
		});

		expression.represent(representation);
	}

	public enum UnaryOp { Delete, Void, Typeof, UnaryPlus, UnaryMinus, BitwiseNot, LogicalNot, Await }
}