package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.LanguageError;

public record UnaryExpression(Expression expression, xyz.lebster.node.expression.UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return switch (op) {
			case Add -> expression.execute(interpreter).toNumericLiteral(interpreter);
			case LogicalNot -> expression.execute(interpreter).toBooleanLiteral(interpreter).not();
			case Negate -> expression.execute(interpreter).toNumericLiteral(interpreter).unaryMinus();
			case Typeof -> {
				if (!(expression instanceof final LeftHandSideExpression lhs)) {
					throw new AbruptCompletion(new LanguageError("Invalid right-hand side in 'typeof' expression"), AbruptCompletion.Type.Throw);
				}

//				https://tc39.es/ecma262/multipage#sec-typeof-operator-runtime-semantics-evaluation
				final Reference reference = lhs.toReference(interpreter);
				if (reference.isResolvable()) {
					yield new StringLiteral(reference.getValue(interpreter).typeOf());
				} else {
					yield new StringLiteral("undefined");
				}
 			}
		};
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "UnaryExpression");
		Dumper.dumpIndicated(indent + 1, "Expression", expression);
		Dumper.dumpEnum(indent + 1, "Operator", op);
	}

	public enum UnaryOp {
		Negate, LogicalNot, Add, Typeof
	}
}
