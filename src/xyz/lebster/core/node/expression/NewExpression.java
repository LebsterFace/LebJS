package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Constructor;

public record NewExpression(Expression constructExpr, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatenew")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = constructExpr.execute(interpreter);
		final Value<?>[] executedArguments = arguments.executeAll(interpreter).toArray(new Value[0]);
		return getConstructor(value).construct(interpreter, executedArguments);
	}

	private Constructor getConstructor(Value<?> exprValue) throws AbruptCompletion {
		if (exprValue instanceof final Constructor constructor) {
			return constructor;
		} else {
			final var representation = new StringRepresentation();
			exprValue.display(representation);
			representation.append(" is not a constructor");
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "NewExpression");
		Dumper.dumpIndicated(indent + 1, "Construct Expression", constructExpr);
		Dumper.dumpIndicator(indent + 1, arguments.isEmpty() ? "No Arguments" : "Arguments");
		arguments.dumpWithoutIndices(indent + 1);
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