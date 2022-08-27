package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

public record EqualityExpression(Expression left, Expression right, EqualityOp op) implements Expression {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.binaryExpression(this, left, op, right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-equality-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);

		return BooleanValue.of(switch (op) {
			case StrictEquals -> left_value.equals(right_value);
			case StrictNotEquals -> !left_value.equals(right_value);
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