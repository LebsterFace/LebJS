package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.Interpreter;

public final class ReferenceError extends ErrorObject {

	public ReferenceError(Interpreter interpreter, String message) {
		super(interpreter, message);
	}
}