package xyz.lebster.core.expression;

import xyz.lebster.ANSI;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Executable;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;


public class CallExpression implements Expression {
	public final Expression callee;
	public final Expression[] arguments;

	public CallExpression(Expression callee, Expression... args) {
		this.callee = callee;
		this.arguments = args;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "CallExpression");
		Interpreter.dumpName(indent + 1, "Callee");
		callee.dump(indent + 2);

		if (arguments.length > 0) {
			Interpreter.dumpName(indent + 1, "Arguments");
		} else {
			Interpreter.dumpIndent(indent + 1);
			System.out.print(ANSI.RED);
			System.out.print("[[NO ARGS]]");
			System.out.println(ANSI.RESET);
		}

		for (Expression argument : arguments) {
			argument.dump(indent + 2);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final CallFrame frame = callee.toCallFrame(interpreter);

		if (!(frame.executedCallee() instanceof final Executable<?> executable)) {
			throw new LanguageException(frame.executedCallee().getClass().getCanonicalName() + " is not a function");
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
