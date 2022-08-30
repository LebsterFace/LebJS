package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.Interpreter;

public class CheckedError extends ErrorObject {
	public CheckedError(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.errorPrototype, message);
	}
}
