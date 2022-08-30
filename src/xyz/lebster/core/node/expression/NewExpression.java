package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Constructor;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record NewExpression(Expression constructExpr, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatenew")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = constructExpr.execute(interpreter);
		final Value<?>[] executedArguments = arguments == null ? new Value[0] : arguments.executeAll(interpreter).toArray(new Value[0]);
		if (value instanceof final Constructor constructor) {
			return constructor.construct(interpreter, executedArguments, constructor);
		} else {
			final String message = constructExpr.toRepresentationString() + " is not a constructor";
			throw error(new TypeError(interpreter, message));
		}
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Construct Expression", constructExpr)
			.expressionList("Arguments", arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("new ");
		constructExpr.represent(representation);
		if (arguments != null) {
			representation.append('(');
			arguments.represent(representation);
			representation.append(')');
		}
	}
}