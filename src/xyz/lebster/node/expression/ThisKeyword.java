package xyz.lebster.node.expression;

import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.value.Value;

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