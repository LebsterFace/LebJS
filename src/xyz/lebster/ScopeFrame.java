package xyz.lebster;

import xyz.lebster.node.ScopeNode;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

public class ScopeFrame {
	public final ScopeNode node;
	// FIXME: Other types of scope endings exist
	public boolean didReturn = false;
	public Value<?> returned = null;

	public ScopeFrame(ScopeNode node) {
		this.node = node;
	}

	public void doReturn(Value<?> value) {
		this.returned = value;
		this.didReturn = true;
	}
}
