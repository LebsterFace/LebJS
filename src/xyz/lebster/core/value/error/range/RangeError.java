package xyz.lebster.core.value.error.range;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.error.ErrorObject;

public final class RangeError extends ErrorObject {
	public RangeError(Interpreter interpreter, String message) {
		super(interpreter, interpreter.intrinsics.rangeErrorPrototype, message);
	}
}