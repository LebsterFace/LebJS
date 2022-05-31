package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.Declarable;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.string.StringValue;

import java.util.List;

public sealed interface DestructuringAssignmentTarget extends Dumpable, Assignable, Declarable
	permits ArrayDestructuring, IdentifierExpression, ObjectDestructuring {
	List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) throws AbruptCompletion;

	@Override
	default Value<?> assign(Interpreter interpreter, Expression rhs) throws AbruptCompletion {
		final Value<?> value = getValue(interpreter, rhs);
		for (var binding : this.getBindings(interpreter, value)) {
			final Reference reference = interpreter.getBinding(binding.name());
			reference.putValue(interpreter, binding.value());
		}

		return value;
	}

	@Override
	default void declare(Interpreter interpreter, VariableDeclaration.Kind kind, Expression init) throws AbruptCompletion {
		final Value<?> value = getValue(interpreter, init);
		for (var binding : this.getBindings(interpreter, value))
			interpreter.declareVariable(kind, binding.name(), binding.value());
	}

	private Value<?> getValue(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		if (expression == null) return Undefined.instance;

		final Value<?> value = expression.execute(interpreter);
		if (this instanceof final IdentifierExpression identifierAssignmentTarget) {
			final StringValue name = identifierAssignmentTarget.name();
			if (Executable.isAnonymousFunctionExpression(expression)) {
				if (value instanceof final Executable function) {
					function.set(interpreter, Names.name, name);
					function.updateName(name.toFunctionName());
				}
			}
		}

		return value;
	}
}