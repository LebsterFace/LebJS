package xyz.lebster.core.value.error.reference;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.error.ErrorObject;

public final class ReferenceError extends ErrorObject {
	public ReferenceError(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.referenceErrorPrototype, message);
	}
}