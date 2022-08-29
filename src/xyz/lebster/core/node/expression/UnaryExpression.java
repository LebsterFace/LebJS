package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
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
import xyz.lebster.core.value.primitive.string.StringValue;

public record UnaryExpression(Expression expression, UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case UnaryPlus -> expression.execute(interpreter).toNumberValue(interpreter);
			case UnaryMinus -> expression.execute(interpreter).toNumberValue(interpreter).unaryMinus();
			case LogicalNot -> expression.execute(interpreter).toBooleanValue(interpreter).not();
			case Typeof -> {
				// https://tc39.es/ecma262/multipage#sec-typeof-operator-runtime-semantics-evaluation
				if (expression instanceof final LeftHandSideExpression lhs) {
					final Reference reference = lhs.toReference(interpreter);
					if (reference.isResolvable()) {
						yield new StringValue(reference.getValue(interpreter).typeOf(interpreter));
					} else {
						yield Names.undefined;
					}
				} else {
					yield new StringValue(expression.execute(interpreter).typeOf(interpreter));
				}
			}

			// https://tc39.es/ecma262/multipage#sec-void-operator-runtime-semantics-evaluation
			case Void -> {
				// UnaryExpression : void UnaryExpression
				// 1. Let expr be the result of evaluating UnaryExpression.
				// 2. Perform ? GetValue(expr).
				expression.execute(interpreter);
				// 3. Return undefined.
				yield Undefined.instance;
			}

			case Delete -> deleteOperator(interpreter, expression);

			case BitwiseNot -> throw new NotImplemented("The `~` operator");
			case Await -> throw new NotImplemented("The `await` operator");
		};
	}

	@NonCompliant
	private BooleanValue deleteOperator(Interpreter interpreter, Expression expression) throws AbruptCompletion {
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