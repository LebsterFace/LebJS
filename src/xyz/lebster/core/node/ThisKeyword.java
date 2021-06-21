package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public class ThisKeyword extends StaticIdentifier {
	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		return interpreter.thisValue();
	}

	@Override
	public String getName() {
		return "this";
	}
}
