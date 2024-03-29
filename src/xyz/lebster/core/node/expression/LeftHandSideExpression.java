package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.value.Value;

public interface LeftHandSideExpression extends Expression, AssignmentTarget {
	Reference toReference(Interpreter interpreter) throws AbruptCompletion;

	default Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		final Reference ref = this.toReference(interpreter);
		ref.putValue(interpreter, value);
		return value;
	}
}