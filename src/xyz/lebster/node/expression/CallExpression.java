package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.Executable;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.TypeError;

public record CallExpression(Expression callee, Expression... arguments) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function-calls-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?>[] executedArguments = new Value[arguments.length];
		for (int i = 0; i < arguments.length; i++) executedArguments[i] = arguments[i].execute(interpreter);
		final ExecutionContext frame = callee.toExecutionContext(interpreter);
		if (frame.executedCallee() instanceof final Executable<?> executable) {
			interpreter.enterExecutionContext(frame);
			try {
				return executable.call(interpreter, executedArguments);
			} finally {
				interpreter.exitExecutionContext(frame);
			}
		} else {
			return new TypeError(callee.getClass().getSimpleName() + " is not a function");
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "CallExpression");
		Dumper.dumpIndicated(indent + 1, "Callee", callee);
		Dumper.dumpIndicator(indent + 1, arguments.length > 0 ? "Arguments" : "No Arguments");
		for (Expression argument : arguments) argument.dump(indent + 2);
	}
}