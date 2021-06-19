package xyz.lebster.core.node;

import java.util.ArrayList;

abstract public class ScopeNode implements ASTNode {
	public final ArrayList<ASTNode> children = new ArrayList<>();

	public void append(ASTNode node) {
		this.children.add(node);
	}
}
