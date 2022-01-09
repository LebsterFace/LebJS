package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.Constructor;
import xyz.lebster.core.runtime.error.TypeError;

public record NewExpression(Expression constructExpr, Expression... arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatenew")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final var value = constructExpr.execute(interpreter);

		final Value<?>[] argList = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++)
			argList[i] = arguments[i].execute(interpreter);

		return getConstructor(value).construct(interpreter, argList);
	}

	private Constructor<?> getConstructor(Value<?> exprValue) throws AbruptCompletion {
		if (exprValue instanceof final Constructor<?> constructor) {
			return constructor;
		} else {
			final StringBuilder builder = new StringBuilder();
			exprValue.display(builder);
			builder.append(" is not a constructor");
			throw AbruptCompletion.error(new TypeError(builder.toString()));
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "NewExpression");
		Dumper.dumpIndicated(indent + 1, "constructExpr", constructExpr);
		Dumper.dumpIndicator(indent + 1, "arguments");
		for (final Expression argument : arguments) argument.dump(indent + 2);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("new ");
		constructExpr.represent(representation);
		representation.append('(');
		if (arguments.length > 0) {
			arguments[0].represent(representation);
			for (int i = 1; i < arguments.length; i++) {
				representation.append(", ");
				arguments[i].represent(representation);
			}
		}
		representation.append(')');
	}
}