package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Identifier;

public abstract class FunctionNode implements ASTNode {
	public final Identifier name;
	public final Identifier[] arguments;
	public final BlockStatement body;

	public FunctionNode(BlockStatement body, Identifier name, Identifier... arguments) {
		this.name = name;
		this.arguments = arguments;
		this.body = body;
	}

	public String getCallString() {
		final StringBuilder builder = new StringBuilder(name == null ? "" : name.value());

		builder.append('(');
		if (arguments.length > 0) {
			builder.append(arguments[0].value());
			for (int i = 1; i < arguments.length; i++) {
				builder.append(", ");
				builder.append(arguments[i].value());
			}
		}
		builder.append(')');

		return builder.toString();
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, getClass().getSimpleName(), getCallString());
		for (final ASTNode child : body.children()) {
			child.dump(indent + 1);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representation.append(name == null ? "" : name.value());
		representation.append('(');
		if (arguments.length > 0) {
			representation.append(arguments[0].value());
			for (int i = 1; i < arguments.length; i++) {
				representation.append(", ");
				representation.append(arguments[i].value());
			}
		}
		representation.append(") ");
		body.represent(representation);
	}

}