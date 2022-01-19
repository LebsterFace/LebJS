package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;

public record CallExpression(Expression callee, Expression... arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-calls-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] executedArguments = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) executedArguments[i] = arguments[i].execute(interpreter);

		final ExecutionContext frame = callee.toExecutionContext(interpreter);
		final var executable = Executable.getExecutable(frame.executedCallee());
		return executable.callWithContext(interpreter, frame, executedArguments);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "CallExpression");
		Dumper.dumpIndicated(indent + 1, "Callee", callee);
		Dumper.dumpIndicator(indent + 1, arguments.length > 0 ? "Arguments" : "No Arguments");
		for (Expression argument : arguments) argument.dump(indent + 2);
	}

	@Override
	public void represent(StringRepresentation representation) {
		callee.represent(representation);
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