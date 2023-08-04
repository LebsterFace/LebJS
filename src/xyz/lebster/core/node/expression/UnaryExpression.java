package xyz.lebster.core.node.expression;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.NumericValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigInteger;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-unary-operators")
public record UnaryExpression(SourceRange range, Expression expression, UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case LogicalNot -> expression.execute(interpreter).toBooleanValue(interpreter).not();
			case UnaryPlus -> expression.execute(interpreter).toNumberValue(interpreter);
			case UnaryMinus -> unaryMinusOperator(interpreter);
			case Typeof -> typeofOperator(interpreter);
			case Void -> voidOperator(interpreter);
			case Delete -> deleteOperator(interpreter);
			case BitwiseNot -> bitwiseNotOperator(interpreter);

			case Await -> throw new NotImplemented("The `await` operator");
		};
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-unary-minus-operator")
	private NumericValue<?> unaryMinusOperator(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let expr be ? Evaluation of UnaryExpression.
		final Value<?> expr = expression.execute(interpreter);
		// 2. Let oldValue be ? ToNumeric(? GetValue(expr)).
		final NumericValue<?> oldValue = expr.toNumeric(interpreter);
		// 3. If oldValue is a Number, then
		if (oldValue instanceof final NumberValue N) {
			// a. Return Number::unaryMinus(oldValue).
			return new NumberValue(-N.value);
		}
		// 4. Else,
		else {
			// a. Assert: oldValue is a BigInt.
			if (!(oldValue instanceof final BigIntValue B)) throw new ShouldNotHappen("Invalid numeric value");
			// b. Return BigInt::unaryMinus(oldValue).
			return new BigIntValue(B.value.negate());
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-bitwise-not-operator")
	private NumericValue<?> bitwiseNotOperator(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let expr be ? Evaluation of UnaryExpression.
		final Value<?> expr = expression.execute(interpreter);
		// 2. Let oldValue be ? ToNumeric(? GetValue(expr)).
		final NumericValue<?> oldValue = expr.toNumeric(interpreter);
		// 3. If oldValue is a Number, then
		if (oldValue instanceof final NumberValue N) {
			// a. Return Number::bitwiseNOT(oldValue).
			return new NumberValue(~N.toInt32());
		}
		// 4. Else,
		else {
			// a. Assert: oldValue is a BigInt.
			if (!(oldValue instanceof final BigIntValue B)) throw new ShouldNotHappen("Invalid numeric value");
			// b. Return BigInt::bitwiseNOT(oldValue).
			return new BigIntValue(B.value.negate().subtract(BigInteger.ONE));
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-typeof-operator")
	private StringValue typeofOperator(Interpreter interpreter) throws AbruptCompletion {
		if (expression instanceof final LeftHandSideExpression lhs) {
			final Reference reference = lhs.toReference(interpreter);
			if (reference.isResolvable()) {
				return new StringValue(reference.getValue(interpreter).typeOf());
			} else {
				return Names.undefined;
			}
		} else {
			return new StringValue(expression.execute(interpreter).typeOf());
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

	public enum UnaryOp { Delete, Void, Typeof, UnaryPlus, UnaryMinus, BitwiseNot, LogicalNot, Await }
}