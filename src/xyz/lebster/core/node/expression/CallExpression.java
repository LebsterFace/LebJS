package xyz.lebster.core.node.expression;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.Executable;

public record CallExpression(Expression callee, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-calls-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] executedArguments = arguments.executeAll(interpreter).toArray(new Value[0]);

		if (callee instanceof final MemberExpression memberExpression) {
			// toReference is being used to handle executing the base, property, and lookup in one
			final Reference reference = memberExpression.toReference(interpreter);
			final Executable executable = getExecutable(interpreter, reference.getValue(interpreter));
			return executable.call(interpreter, reference.base(), executedArguments);
		} else {
			final Executable executable = getExecutable(interpreter, callee.execute(interpreter));
			return executable.call(interpreter, executedArguments);
		}
	}

	private Executable getExecutable(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable executable)
			return executable;

		final String message = ANSI.stripFormatting(callee.toRepresentationString()) + " is not a function";
		throw AbruptCompletion.error(new TypeError(interpreter, message));
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Callee", callee)
			.expressionList("Arguments", arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		callee.represent(representation);
		representation.append('(');
		arguments.represent(representation);
		representation.append(')');
	}
}