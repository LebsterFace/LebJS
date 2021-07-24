package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;

public final class ThisKeyword extends StaticIdentifier {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.thisValue();
	}

	@Override
	public String getName() {
		return "this";
	}
}