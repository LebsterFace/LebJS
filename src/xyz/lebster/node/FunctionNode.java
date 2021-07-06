package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.node.expression.Identifier;

public abstract class FunctionNode implements ASTNode {
	public final Identifier name;
	public final Identifier[] arguments;
	public final BlockStatement body;

	public FunctionNode(BlockStatement body, Identifier name, Identifier... arguments) {
		this.name = name;
		this.arguments = arguments;
		this.body = body;
	}

	public FunctionNode(Identifier name, Identifier... arguments) {
		this(new BlockStatement(), name, arguments);
	}

	public String getCallString() {
		final StringBuilder builder = new StringBuilder(name.value());

		builder.append("(");
		if (arguments.length > 0) {
			builder.append(arguments[0].value());
			for (int i = 1; i < arguments.length; i++) {
				builder.append(", ");
				builder.append(arguments[i].value());
			}
		}
		builder.append(")");

		return builder.toString();
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, getClass().getSimpleName(), getCallString());
		for (final ASTNode child : body.children()) {
			child.dump(indent + 1);
		}
	}
}
