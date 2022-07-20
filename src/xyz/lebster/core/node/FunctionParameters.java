package xyz.lebster.core.node;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.AssignmentTarget;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.List;

public final class FunctionParameters implements Dumpable {
	private final List<Parameter> parameterList = new ArrayList<>();
	public AssignmentTarget rest;

	public void addWithDefault(AssignmentTarget target, Expression defaultExpression) {
		this.parameterList.add(new Parameter(target, defaultExpression));
	}

	public void add(AssignmentTarget target) {
		this.parameterList.add(new Parameter(target, null));
	}

	public FunctionParameters() {
	}

	public FunctionParameters(AssignmentTarget... assignmentTargets) {
		for (final AssignmentTarget p : assignmentTargets) {
			this.add(p);
		}
	}

	private record Parameter(AssignmentTarget target, Expression defaultExpression) implements Dumpable {
		@Override
		public void dump(int indent) {
			DumpBuilder.begin(indent)
				.self(this)
				.child("Target", target)
				.optional("Default Expression", defaultExpression);
		}

		@Override
		public void represent(StringRepresentation representation) {
			target.represent(representation);
			if (defaultExpression != null) {
				representation.append(" = ");
				defaultExpression.represent(representation);
			}
		}

		public void declare(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
			target.declare(interpreter, VariableDeclaration.Kind.Let, value);
		}

		public void declare(Interpreter interpreter) throws AbruptCompletion {
			if (defaultExpression != null) {
				target.declare(interpreter, VariableDeclaration.Kind.Let, defaultExpression);
			} else {
				target.declare(interpreter, VariableDeclaration.Kind.Let, Undefined.instance);
			}
		}
	}

	@Override
	public void dump(int indent) {
		DumpBuilder
			.begin(indent)
			.self(this)
			.children("Arguments", parameterList);
	}

	@Override
	public void represent(StringRepresentation representation) {
		if (parameterList.size() > 0) {
			representation.append(parameterList.get(0));
			for (int i = 1; i < parameterList.size(); i++) {
				representation.append(", ");
				representation.append(parameterList.get(i));
			}
		}
	}

	public void declareArguments(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		int i = 0;
		for (; i < parameterList.size() && i < passedArguments.length; i++)
			parameterList.get(i).declare(interpreter, passedArguments[i]);
		if (rest == null) {
			for (; i < parameterList.size(); i++)
				parameterList.get(i).declare(interpreter);
		} else {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			for (; i < passedArguments.length; i++)
				restValues.add(passedArguments[i]);
			final ArrayObject restArray = new ArrayObject(interpreter, restValues);
			rest.declare(interpreter, VariableDeclaration.Kind.Let, restArray);
		}
	}
}
