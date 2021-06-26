package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

import java.util.ArrayList;
import java.util.List;

abstract public class ScopeNode implements ASTNode {
	public final ArrayList<ASTNode> children = new ArrayList<>();

	public void append(ASTNode node) {
		children.add(node);
	}

	public void append(List<ASTNode> nodes) {
		children.addAll(nodes);
	}

	public Value<?> executeChildren(Interpreter interpreter) throws LanguageException {
		Value<?> lastValue = new Undefined();
		for (ASTNode node : children) lastValue = node.execute(interpreter);
		return lastValue;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		return executeChildren(interpreter);
	}
}
