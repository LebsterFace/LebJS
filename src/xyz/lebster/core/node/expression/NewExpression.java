package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Constructor;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.TypeError;


public record NewExpression(Expression constructExpr, Expression... arguments) implements Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
//		FIXME: Follow spec
		final Value<?>[] executedArguments = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) executedArguments[i] = arguments[i].execute(interpreter);

		final Value<?> exprValue = constructExpr.execute(interpreter);
		final Constructor<?> constructor = getConstructor(exprValue);

		return constructor.construct(interpreter, executedArguments);
	}

	private Constructor<?> getConstructor(Value<?> exprValue) throws AbruptCompletion {
		if (exprValue instanceof final Constructor<?> constructor) {
			return constructor;
		} else {
			throw AbruptCompletion.error(new TypeError("Not a constructor!"));
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "NewExpression");
		Dumper.dumpIndicated(indent + 1, "constructExpr", constructExpr);
		Dumper.dumpIndicator(indent + 1, "arguments");
		for (final Expression argument : arguments) argument.dump(indent + 2);
	}
}