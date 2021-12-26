package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SpecificationURL;
import xyz.lebster.core.node.value.BooleanLiteral;
import xyz.lebster.core.node.value.Value;

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

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(switch (op) {
			case StrictEquals -> "===";
			case StrictNotEquals -> "!==";
			case LooseEquals -> "==";
			case LooseNotEquals -> "!=";
		});
		representation.append(' ');
		right.represent(representation);
	}

	public enum EqualityOp {
		StrictEquals, StrictNotEquals,
		LooseEquals, LooseNotEquals
	}
}