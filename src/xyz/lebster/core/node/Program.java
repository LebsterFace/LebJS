package xyz.lebster.core.node;

public class Program extends ScopeNode {
	@Override
	public void dump(int indent) {
		for (ASTNode child : children) {
			child.dump(indent);
		}
	}
}
