package xyz.lebster.core.value.error.syntax;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.error.ErrorObject;

public final class SyntaxErrorObject extends ErrorObject {
	public SyntaxErrorObject(Interpreter interpreter, String message) {
		super(interpreter.intrinsics.syntaxErrorPrototype, message);
	}

	@Override
	protected String getName() {
		return "SyntaxError";
	}
}