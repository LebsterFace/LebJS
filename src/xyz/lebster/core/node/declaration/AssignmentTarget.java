package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.Declarable;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.List;

public sealed interface AssignmentTarget extends Dumpable, Assignable, Declarable
	permits ArrayDestructuring, IdentifierExpression, ObjectDestructuring {
	List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion;

	default Value<?> assign(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		return this.assign(interpreter, getValue(interpreter, expression));
	}

	default void declare(Interpreter interpreter, Kind kind, Expression expression) throws AbruptCompletion {
		this.declare(interpreter, kind, getValue(interpreter, expression));
	}

	@Override
	default Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		for (var binding : this.getBindings(interpreter, value)) {
			final Reference reference = interpreter.getBinding(binding.name());
			reference.putValue(interpreter, binding.value());
		}

		return value;
	}

	default void declare(Interpreter interpreter, Kind kind, Value<?> value) throws AbruptCompletion {
		for (var binding : this.getBindings(interpreter, value))
			interpreter.declareVariable(kind, binding.name(), binding.value());
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