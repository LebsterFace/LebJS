package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.node.declaration.Kind;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.value.Value;

public record ForBinding(Kind kind, AssignmentTarget assignmentTarget) implements Assignable {
	public ForBinding(VariableDeclaration declaration) {
		this(declaration.kind(), declaration.declarations()[0].target());
	}

	@Override
	public Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		assignmentTarget.declare(interpreter, kind, value);
		return value;
	}
}
