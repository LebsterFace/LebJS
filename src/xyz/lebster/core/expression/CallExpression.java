package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Executable;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.exception.TypeError;

public class CallExpression extends Expression {
	public final Expression callee;
	public final Expression[] arguments;

	public CallExpression(Expression callee, Expression... args) {
		this.callee = callee;
		this.arguments = args;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("CallExpression:");
		Interpreter.dumpIndent(indent + 1);
		System.out.println("Callee:");
		callee.dump(indent + 2);
		Interpreter.dumpIndent(indent + 1);
		System.out.println(arguments.length > 0 ? "Arguments:" : "[[NO ARGS]]");
		for (Expression argument : arguments) {
			argument.dump(indent + 2);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final CallFrame frame = callee.toCallFrame(interpreter);

		if (!(frame.executedCallee() instanceof final Executable<?> executable)) {
			throw new TypeError(frame.executedCallee().getClass().getCanonicalName() + " is not a function");
		}

		final Value<?>[] args = new Value<?>[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			args[i] = arguments[i].execute(interpreter);
		}

		interpreter.enterCallFrame(frame);
		final Value<?> returnValue = executable.executeChildren(interpreter, args);
		interpreter.exitCallFrame();
		return returnValue;
	}
}
