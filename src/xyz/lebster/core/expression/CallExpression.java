package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.TypeError;
import xyz.lebster.core.value.Executable;
import xyz.lebster.core.value.Value;


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
		Interpreter.dumpIndicated(indent + 1, "Callee", callee);
		Interpreter.dumpIndicator(indent + 1, arguments.length > 0 ? "Arguments" : "No Arguments");
		for (Expression argument : arguments) argument.dump(indent + 2);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final CallFrame frame = callee.toCallFrame(interpreter);

		if (!(frame.executedCallee() instanceof final Executable<?> executable)) {
			return interpreter.throwValue(new TypeError(frame.executedCallee().getClass().getCanonicalName() + " is not a function"));
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
