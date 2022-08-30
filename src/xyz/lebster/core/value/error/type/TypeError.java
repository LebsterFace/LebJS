package xyz.lebster.core.value.error.type;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.error.ErrorObject;

public final class TypeError extends ErrorObject {
	public TypeError(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.typeErrorPrototype, message);
	}
}