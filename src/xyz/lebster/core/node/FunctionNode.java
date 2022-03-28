package xyz.lebster.core.node;

import xyz.lebster.core.node.statement.BlockStatement;

public interface FunctionNode extends ASTNode {
	String name();

	String[] arguments();

	BlockStatement body();

	default String toCallString() {
		final String name = name();
		final String[] arguments = arguments();

		final StringBuilder builder = new StringBuilder(name == null ? "" : name);
		builder.append('(');
		if (arguments.length > 0) {
			builder.append(arguments[0]);
			for (int i = 1; i < arguments.length; i++) {
				builder.append(", ");
				builder.append(arguments[i]);
			}
		}
		builder.append(')');
		return builder.toString();
	}
}