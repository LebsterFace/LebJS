package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.Declarable;
import xyz.lebster.core.node.Representable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

public interface AssignmentTarget extends Assignable, Declarable, Representable {
	default Value<?> assign(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		return this.assign(interpreter, getValue(interpreter, expression));
	}

	default void declare(Interpreter interpreter, Kind kind, Expression expression) throws AbruptCompletion {
		this.declare(interpreter, kind, getValue(interpreter, expression));
	}

	private Value<?> getValue(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		if (expression == null) return Undefined.instance;

		if (this instanceof final IdentifierExpression identifierAssignmentTarget) {
			final StringValue name = identifierAssignmentTarget.name();
			return Executable.namedEvaluation(interpreter, expression, name);
		}

		return expression.execute(interpreter);
	}
}