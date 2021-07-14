package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.NumericLiteral;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.LanguageError;

public record UnaryExpression(Expression expression, UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case Add -> expression.execute(interpreter).toNumericLiteral();
			case LogicalNot -> expression.execute(interpreter).toBooleanLiteral().not();
			case Negate -> expression.execute(interpreter).toNumericLiteral().unaryMinus();
			case Typeof -> {
				if (!(expression instanceof final LeftHandSideExpression lhs)) {
					throw AbruptCompletion.error(new LanguageError("Invalid right-hand side in 'typeof' expression"));
				}

//				https://tc39.es/ecma262/multipage#sec-typeof-operator-runtime-semantics-evaluation
				final Reference reference = lhs.toReference(interpreter);
				if (reference.isResolvable()) {
					yield new StringLiteral(reference.getValue(interpreter).typeOf());
				} else {
					yield new StringLiteral("undefined");
				}
			}

//			https://tc39.es/ecma262/multipage#sec-postfix-increment-operator
			case PostIncrement, PostDecrement, PreIncrement, PreDecrement -> {
				if (!(expression instanceof final LeftHandSideExpression lhs)) {
					throw AbruptCompletion.error(new LanguageError("Invalid left-hand side expression in postfix operation"));
				}

				final Reference lref = lhs.toReference(interpreter);
				final NumericLiteral oldValue = lhs.execute(interpreter).toNumericLiteral();
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