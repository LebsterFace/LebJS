package xyz.lebster.core.value.error;

import xyz.lebster.core.interpreter.Interpreter;

public final class RangeError extends ErrorObject {
	public RangeError(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.rangeErrorPrototype, message);
	}
}