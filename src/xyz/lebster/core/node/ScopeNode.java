package xyz.lebster.core.node;

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
}
