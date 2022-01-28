package xyz.lebster.core.node;

import xyz.lebster.core.node.statement.Statement;

public interface AppendableNode extends ASTNode {
	void append(Statement node);
}