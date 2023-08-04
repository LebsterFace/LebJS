package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.Declarable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

/**
 * A container for an assignment target and a default expression
 * Used to avoid storing defaultExpression on AssignmentTarget for the sake of IdentifierExpression
 */
public record AssignmentPattern(AssignmentTarget assignmentTarget, Expression defaultExpression) implements Assignable, Declarable {
	@Override
	public Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		if (value == Undefined.instance && defaultExpression != null) {
			return assignmentTarget.assign(interpreter, defaultExpression);
		} else {
			return assignmentTarget.assign(interpreter, value);
		}
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> value) throws AbruptCompletion {
		if (value == Undefined.instance && defaultExpression != null) {
			assignmentTarget.declare(interpreter, kind, defaultExpression);
		} else {
			assignmentTarget.declare(interpreter, kind, value);
		}
	}
}