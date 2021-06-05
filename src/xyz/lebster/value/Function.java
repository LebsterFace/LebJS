package xyz.lebster.value;

import xyz.lebster.node.ScopeNode;

public class Function extends Value<ScopeNode> {
	public Function(ScopeNode value) {
		super(Type.Function, value);
	}
}
