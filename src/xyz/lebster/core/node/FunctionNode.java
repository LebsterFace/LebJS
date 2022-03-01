package xyz.lebster.core.node;

import xyz.lebster.core.node.statement.BlockStatement;

public interface FunctionNode extends ASTNode {
	String name();
	String[] arguments();
	BlockStatement body();
}