package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public class Program extends ScopeNode {
	@Override
	public void dump(int indent) {
		for (ASTNode child : children) child.dump(indent);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		Value<?> lastValue = new Undefined();
		for (ASTNode node : children) lastValue = node.execute(interpreter);
		return lastValue;
	}
}
