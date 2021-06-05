package xyz.lebster.core.value;

import xyz.lebster.core.node.ScopeNode;

public class Function extends Value<ScopeNode> {
	public Function(ScopeNode value) {
		super(Type.Function, value);
	}
}
