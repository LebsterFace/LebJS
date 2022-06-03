package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record VariableDeclarator(DestructuringAssignmentTarget target, Expression init, SourceRange range) implements ASTNode {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Target", target)
			.optional("Initializer", init);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		target.declare(interpreter, VariableDeclaration.Kind.Let, init);
		return Undefined.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("let ");
		target.represent(representation);
		if (init != null) {
			representation.append(" = ");
			init.represent(representation);
		}

		representation.append(';');
	}
}