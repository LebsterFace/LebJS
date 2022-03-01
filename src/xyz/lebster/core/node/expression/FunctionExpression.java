package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Function;

public record FunctionExpression(BlockStatement body, String name, String[] arguments) implements FunctionNode, Expression {
	@Override
	public Value<?> execute(Interpreter interpreter) {
		return new Function(this, interpreter.lexicalEnvironment());
	}

	@Override
	public void dump(int indent) {
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

		Dumper.dumpParameterized(indent, getClass().getSimpleName(), builder.toString());
		for (final ASTNode child : body.children()) {
			child.dump(indent + 1);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("function ");
		representation.append(name == null ? "" : name);
		representation.append('(');
		if (arguments.length > 0) {
			representation.append(arguments[0]);
			for (int i = 1; i < arguments.length; i++) {
				representation.append(", ");
				representation.append(arguments[i]);
			}
		}
		representation.append(") ");
		body.represent(representation);
	}
}