package xyz.lebster.core.node.declaration;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.Expression;

/**
 * A container for an assignment target and a default expression
 * Used to avoid storing defaultExpression on AssignmentTarget for the sake of IdentifierExpression
 */
public record AssignmentPattern(AssignmentTarget assignmentTarget, Expression defaultExpression) implements Dumpable {
	@Override
	public void dump(int indent) {
		if (defaultExpression != null) {
			DumpBuilder.begin(indent)
				.self(this)
				.child("Target", assignmentTarget)
				.child("Default", defaultExpression);
		} else {
			assignmentTarget.dump(indent);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		assignmentTarget.represent(representation);
		if (defaultExpression != null) {
			representation.append(" = ");
			defaultExpression.represent(representation);
		}
	}
}