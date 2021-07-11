package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.Value;

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
			case And -> {
				final boolean lbool = lval.toBooleanLiteral(interpreter).value;
				yield lbool ? right.execute(interpreter) : lval;
			}

			case Or -> {
				final boolean lbool = lval.toBooleanLiteral(interpreter).value;
				yield lbool ? lval : right.execute(interpreter);
			}
		};
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(switch (op) {
			case And -> "&&";
			case Or -> "||";
		});
		representation.append(' ');
		right.represent(representation);
	}

	public enum LogicOp {
		And, Or
	}
}