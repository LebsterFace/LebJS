package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.Interpreter;

public final class TypeError extends ErrorObject {
	public TypeError(Interpreter interpreter, String message) {
		super(interpreter, message);
	}
}