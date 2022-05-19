package xyz.lebster.core.value.error;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;

public final class EvalError extends ErrorObject {
	public final Throwable wrappedThrowable;

	public EvalError(Interpreter interpreter, Throwable e) {
		super(interpreter, e.getMessage());
		this.wrappedThrowable = e;
	}

	@Override
	public String toString() {
		return wrappedThrowable.getClass().getSimpleName() + ": " + message;
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_CYAN);
		representation.append("[");
		representation.append(wrappedThrowable.getClass().getSimpleName());
		representation.append(": ");
		representation.append(message);
		representation.append("]");
		representation.append(ANSI.RESET);
	}
}
