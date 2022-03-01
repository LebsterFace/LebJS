package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Function;
import xyz.lebster.core.runtime.value.primitive.Undefined;

public record FunctionDeclaration(BlockStatement body, String name, String[] arguments) implements FunctionNode, Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Function function = new Function(this, interpreter.lexicalEnvironment());
		interpreter.declareVariable(name, function);
		return Undefined.instance;
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