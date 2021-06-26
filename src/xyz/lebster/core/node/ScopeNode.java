package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

import java.util.ArrayList;
import java.util.List;

abstract public class ScopeNode implements Statement {
	public final ArrayList<ASTNode> children = new ArrayList<>();

	public void append(ASTNode node) {
		children.add(node);
	}

	public void append(List<ASTNode> nodes) {
		children.addAll(nodes);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		Value<?> lastValue = new Undefined();
		for (ASTNode node : children) lastValue = node.execute(interpreter);
		return lastValue;
	}
}
