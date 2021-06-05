package xyz.lebster;

import xyz.lebster.node.Identifier;
import xyz.lebster.node.Program;
import xyz.lebster.node.ScopeNode;
import xyz.lebster.value.Dictionary;
import xyz.lebster.value.Value;

public class ScopeFrame {
	public final ScopeNode node;
	public boolean didExit = false;
	protected Value<?> exitValue = null;
	private Dictionary variables;

	public ScopeFrame(ScopeNode node, Dictionary variables) {
		this.node = node;
		this.variables = variables;
	}

	public ScopeFrame(ScopeNode node) {
		this(node, new Dictionary());
	}

	public void doExit(Value<?> value) {
		this.exitValue = value;
		this.didExit = true;
	}

	public Value<?> getExitValue() {
		return exitValue;
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
