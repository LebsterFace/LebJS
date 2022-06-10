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

public final class FunctionArguments implements Dumpable {
	private final List<ArgumentNode> arguments = new ArrayList<>();
	public AssignmentTarget rest;

	public void addWithDefault(AssignmentTarget target, Expression defaultExpression) {
		this.arguments.add(new ArgumentNode(target, defaultExpression));
	}

	public void add(AssignmentTarget target) {
		this.arguments.add(new ArgumentNode(target, null));
	}

	private record ArgumentNode(AssignmentTarget target, Expression defaultExpression) implements Dumpable {
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
			.children("Arguments", arguments);
	}

	@Override
	public void represent(StringRepresentation representation) {
		if (arguments.size() > 0) {
			representation.append(arguments.get(0));
			for (int i = 1; i < arguments.size(); i++) {
				representation.append(", ");
				representation.append(arguments.get(i));
			}
		}
	}

	public void declareArguments(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		int i = 0;
		for (; i < arguments.size() && i < passedArguments.length; i++)
			arguments.get(i).declare(interpreter, passedArguments[i]);
		if (rest == null) {
			for (; i < arguments.size(); i++)
				arguments.get(i).declare(interpreter);
		} else {
			final ArrayList<Value<?>> restValues = new ArrayList<>();
			for (; i < passedArguments.length; i++)
				restValues.add(passedArguments[i]);
			final ArrayObject restArray = new ArrayObject(interpreter, restValues);
			rest.declare(interpreter, VariableDeclaration.Kind.Let, restArray);
		}
	}
}
