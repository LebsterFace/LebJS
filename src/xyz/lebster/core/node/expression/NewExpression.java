package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Constructor;

public record NewExpression(Expression constructExpr, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatenew")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = constructExpr.execute(interpreter);
		final Value<?>[] executedArguments = arguments == null ? new Value[0] : arguments.executeAll(interpreter).toArray(new Value[0]);
		return getConstructor(interpreter, value).construct(interpreter, executedArguments);
	}

	private Constructor getConstructor(Interpreter interpreter, Value<?> exprValue) throws AbruptCompletion {
		if (exprValue instanceof final Constructor constructor) {
			return constructor;
		} else {
			final var representation = new StringRepresentation();
			exprValue.display(representation);
			representation.append(" is not a constructor");
			throw AbruptCompletion.error(new TypeError(interpreter, representation.toString()));
		}
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent).child("Construct Expression", constructExpr).expressionList("Arguments", arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("new ");
		constructExpr.represent(representation);
		representation.append('(');
		arguments.represent(representation);
		representation.append(')');
	}
}