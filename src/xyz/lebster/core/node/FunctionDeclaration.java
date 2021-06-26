package xyz.lebster.core.node;

import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Function;

public class FunctionDeclaration implements Declaration, Statement {
	public final Identifier name;
	public final Identifier[] arguments;
	public final BlockStatement body;

	public FunctionDeclaration(BlockStatement body, Identifier name, Identifier... arguments) {
		this.name = name;
		this.arguments = arguments;
		this.body = body;
	}

	public FunctionDeclaration(Identifier name, Identifier... arguments) {
		this(new BlockStatement(), name, arguments);
	}

	@Override
	public void dump(int indent) {
		final StringBuilder builder = new StringBuilder(name.value);
		builder.append("(");
		builder.append(arguments[0].value);
		for (int i = 1; i < arguments.length; i++) {
			builder.append(", ");
			builder.append(arguments[i].value);
		}
		builder.append(")");
		Interpreter.dumpParameterized(indent, "FunctionDeclaration", builder.toString());

		for (final ASTNode child : body.children) {
			child.dump(indent + 1);
		}
	}

	@Override
	public Function execute(Interpreter interpreter) {
		final Function value = new Function(this);
		interpreter.declareVariable(name, value);
		return value;
	}
}
