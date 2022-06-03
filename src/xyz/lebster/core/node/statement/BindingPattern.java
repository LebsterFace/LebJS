package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.declaration.DestructuringAssignmentTarget;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.value.Value;

public record BindingPattern(VariableDeclaration.Kind kind, DestructuringAssignmentTarget assignmentTarget) implements Assignable {
	public BindingPattern(VariableDeclaration declaration) {
		this(declaration.kind(), declaration.declarations()[0].target());
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.enum_("Kind", kind)
			.child("Identifier", assignmentTarget);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(kind.name().toLowerCase());
		representation.append(" ");
		assignmentTarget.represent(representation);
	}

	@Override
	public Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		assignmentTarget.declare(interpreter, kind, value);
		return value;
	}
}
