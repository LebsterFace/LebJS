package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.Value;

public record UnaryExpression(Expression expression, xyz.lebster.node.expression.UnaryExpression.UnaryOp op) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = expression.execute(interpreter);
		return switch (op) {
			case Add -> value.toNumericLiteral(interpreter);
			case LogicalNot -> value.toBooleanLiteral(interpreter).not();
			case Negate -> value.toNumericLiteral(interpreter).unaryMinus();
		};
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "UnaryExpression");
		Dumper.dumpIndicated(indent + 1, "Expression", expression);
		Dumper.dumpEnum(indent + 1, "Operator", op);
	}

	public enum UnaryOp {
		Negate, LogicalNot, Add
	}
}
