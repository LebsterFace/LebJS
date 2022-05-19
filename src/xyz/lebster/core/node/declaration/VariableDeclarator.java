package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.string.StringValue;
import xyz.lebster.core.value.globals.Undefined;

public record VariableDeclarator(String identifier, Expression init, SourceRange range) implements ASTNode {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.selfNamed(this, identifier)
			.optional("Initializer", init);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = init == null ? Undefined.instance : init.execute(interpreter);
		final StringValue name = new StringValue(identifier);

		if (Executable.isAnonymousFunctionExpression(init)) {
			if (value instanceof final Executable function) {
				function.set(interpreter, Names.name, name);
				function.updateName(name.toFunctionName());
			}
		}

		interpreter.declareVariable(name, value);
		return Undefined.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("let ");
		representation.append(identifier);
		if (init != null) {
			representation.append(" = ");
			init.represent(representation);
		}

		representation.append(';');
	}
}