package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Constructor;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record NewExpression(SourceRange range, Expression constructExpr, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-evaluatenew")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = constructExpr.execute(interpreter);
		final Value<?>[] executedArguments = arguments == null ? new Value[0] : arguments.executeAll(interpreter);
		if (value instanceof final Constructor constructor) {
			return constructor.construct(interpreter, executedArguments, constructor);
		} else {
			throw error(new TypeError(interpreter, "%s is not a constructor".formatted(constructExpr.range().getText())));
		}
	}
}