package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.NumberValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-update-expressions")
public record UpdateExpression(LeftHandSideExpression expression, UpdateOp op) implements Expression {
	public static final String invalidPostLHS = "Invalid left-hand side expression in postfix operation";
	public static final String invalidPreLHS = "Invalid left-hand side expression in prefix operation";

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-postfix-increment-operator")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = expression.toReference(interpreter);
		final NumberValue oldValue = expression.execute(interpreter).toNumberValue(interpreter);

		final NumberValue newValue = new NumberValue(switch (op) {
			case PostIncrement, PreIncrement -> oldValue.value + 1.0;
			case PostDecrement, PreDecrement -> oldValue.value - 1.0;
		});

		left_reference.putValue(interpreter, newValue);
		return switch (op) {
			case PostIncrement, PostDecrement -> oldValue;
			case PreIncrement, PreDecrement -> newValue;
		};
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "UpdateExpression");
		Dumper.dumpIndicated(indent + 1, "Expression", expression);
		Dumper.dumpEnum(indent + 1, "Operator", op);
	}

	@Override
	public void represent(StringRepresentation representation) {
		switch (op) {
			case PreDecrement -> representation.append("--");
			case PreIncrement -> representation.append("++");
		}

		expression.represent(representation);

		switch (op) {
			case PostDecrement -> representation.append("--");
			case PostIncrement -> representation.append("++");
		}
	}

	public enum UpdateOp { PostIncrement, PostDecrement, PreIncrement, PreDecrement }
}
