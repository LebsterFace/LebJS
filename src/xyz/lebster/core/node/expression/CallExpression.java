package xyz.lebster.core.node.expression;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public record CallExpression(SourceRange range, Expression callee, ExpressionList arguments) implements Expression {
	@Override
	@NonStandard
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-calls-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> func;
		final ObjectValue thisValue;
		if (callee instanceof final MemberExpression memberExpression) {
			// toReference is being used to handle executing the base, property, and lookup in one
			final Reference reference = memberExpression.toReference(interpreter);
			func = reference.getValue(interpreter);
			thisValue = reference.base();
		} else {
			func = callee.execute(interpreter);
			// TODO: OrdinaryCallBindThis
			thisValue = interpreter.globalObject;
		}

		final Value<?>[] executedArguments = arguments.executeAll(interpreter);
		return getExecutable(interpreter, func).call(interpreter, thisValue, executedArguments);
	}

	private Executable getExecutable(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable executable)
			return executable;

		throw error(new TypeError(interpreter, "%s is not a function".formatted(callee.range().getText())));
	}
}