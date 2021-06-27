package xyz.lebster.core.runtime;

import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;

public class ScopeFrame {
	public final ScopeNode node;
	private final Dictionary variables;

	public ScopeFrame(ScopeNode node, Dictionary variables) {
		this.node = node;
		this.variables = variables;
	}

	public ScopeFrame(ScopeNode node) {
		this(node, new Dictionary());
	}

	public Value<?> setVariable(Identifier name, Value<?> value) {
		return variables.set(name, value);
	}

	public Value<?> getVariable(Identifier name) {
		return variables.get(name);
	}

	public boolean containsVariable(Identifier name) {
		return variables.containsKey(name);
	}
}
