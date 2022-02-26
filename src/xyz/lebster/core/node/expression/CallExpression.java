package xyz.lebster.core.node.expression;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;

public record CallExpression(Expression callee, ExpressionList arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-calls-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] executedArguments = arguments.executeAll(interpreter).toArray(new Value[0]);

		if (callee instanceof final MemberExpression memberExpression) {
			// toReference is being used to handle executing the base, property, and lookup in one
			final Reference reference = memberExpression.toReference(interpreter);
			final Executable<?> executable = getExecutable(reference.getValue(interpreter));
			return executable.call(interpreter, reference.base(), executedArguments);
		} else {
			final Executable<?> executable = getExecutable(callee.execute(interpreter));
			return executable.call(interpreter, executedArguments);
		}
	}

	private Executable<?> getExecutable(Value<?> value) throws AbruptCompletion {
		if (value instanceof final Executable<?> executable)
			return executable;

		final String message = ANSI.stripFormatting(callee.toRepresentationString()) + " is not a function";
		throw AbruptCompletion.error(new TypeError(message));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "CallExpression");
		Dumper.dumpIndicated(indent + 1, "Callee", callee);
		Dumper.dumpIndicator(indent + 1, arguments.isEmpty() ? "No Arguments" : "Arguments");
		arguments.dumpWithoutIndices(indent + 1);
	}

	@Override
	public void represent(StringRepresentation representation) {
		callee.represent(representation);
		representation.append('(');
		arguments.represent(representation);
		representation.append(')');
	}
}