package xyz.lebster.core.expression;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public class ThisKeyword extends StaticIdentifier {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.thisValue();
	}

	@Override
	public String getName() {
		return "this";
	}
}
