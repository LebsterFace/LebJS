package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SpecificationURL;
import xyz.lebster.core.node.value.Value;

public record LogicalExpression(Expression left, Expression right, LogicOp op) implements Expression {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "LogicalExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-binary-logical-operators")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> lval = left.execute(interpreter);

		return switch (op) {
			case And -> lval.isTruthy() ? right.execute(interpreter) : lval;
			case Or -> lval.isTruthy() ? lval : right.execute(interpreter);
			case Coalesce -> lval.isNullish() ? right.execute(interpreter) : lval;
		};
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(switch (op) {
			case And -> "&&";
			case Or -> "||";
			case Coalesce -> "??";
		});
		representation.append(' ');
		right.represent(representation);
	}

	public enum LogicOp {
		And, Or, Coalesce
	}
}