package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.BooleanLiteral;
import xyz.lebster.node.value.Value;

public record EqualityExpression(Expression left, Expression right, EqualityOp op) implements Expression {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BinaryExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-equality-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lval = left.execute(interpreter);
		Value<?> rval = right.execute(interpreter);

		return new BooleanLiteral(switch (op) {
			case StrictEquals -> lval.equals(rval);
			case StrictNotEquals -> !lval.equals(rval);
			default -> throw new NotImplemented("EqualityOp: " + op);
		});
	}

	public enum EqualityOp {
		StrictEquals, StrictNotEquals,
		LooseEquals, LooseNotEquals
	}
}