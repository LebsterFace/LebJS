package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.LanguageError;

public record UnaryExpression(Expression expression, UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case Add -> expression.execute(interpreter).toNumericLiteral(interpreter);
			case LogicalNot -> expression.execute(interpreter).toBooleanLiteral(interpreter).not();
			case Negate -> expression.execute(interpreter).toNumericLiteral(interpreter).unaryMinus();
			case Typeof -> {
//				https://tc39.es/ecma262/multipage#sec-typeof-operator-runtime-semantics-evaluation
				if (expression instanceof final LeftHandSideExpression lhs) {
					final Reference reference = lhs.toReference(interpreter);
					if (reference.isResolvable()) {
						yield new StringLiteral(reference.getValue(interpreter).typeOf());
					} else {
						yield new StringLiteral("undefined");
					}
				} else {
					yield new StringLiteral(expression.execute(interpreter).typeOf());
				}
			}

//			https://tc39.es/ecma262/multipage#sec-postfix-increment-operator
			case PostIncrement, PostDecrement, PreIncrement, PreDecrement -> {
				if (!(expression instanceof final LeftHandSideExpression lhs)) {
					throw AbruptCompletion.error(new LanguageError("Invalid left-hand side expression in postfix operation"));
				}

				final Reference lref = lhs.toReference(interpreter);
				final NumericLiteral oldValue = lhs.execute(interpreter).toNumericLiteral(interpreter);
				final NumericLiteral newValue = new NumericLiteral(switch (op) {
					case PostIncrement, PreIncrement -> oldValue.value + 1.0;
					case PostDecrement, PreDecrement -> oldValue.value - 1.0;
					default -> throw new IllegalStateException("Unexpected value: " + op);
				});

				lref.setValue(interpreter, newValue);
				yield switch (op) {
					case PostIncrement, PostDecrement -> oldValue;
					case PreIncrement, PreDecrement -> newValue;
					default -> throw new IllegalStateException("Unexpected value: " + op);
				};
			}
		};
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "UnaryExpression");
		Dumper.dumpIndicated(indent + 1, "Expression", expression);
		Dumper.dumpEnum(indent + 1, "Operator", op);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(switch (op) {
			case Negate -> '-';
			case LogicalNot -> '!';
			case Add -> '+';
			case Typeof -> "typeof ";
			case PreDecrement -> "--";
			case PreIncrement -> "++";
			case PostIncrement, PostDecrement -> "";
		});

		expression.represent(representation);

		switch (op) {
			case PostDecrement -> representation.append("--");
			case PostIncrement -> representation.append("++");
		}
	}

	public enum UnaryOp {
		Negate, LogicalNot, Add, Typeof,
		PostDecrement, PostIncrement,
		PreDecrement, PreIncrement
	}
}