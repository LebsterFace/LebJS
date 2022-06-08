package xyz.lebster.core.node;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.DestructuringAssignmentTarget;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public interface FunctionNode extends ASTNode {
	static void declareArguments(Interpreter interpreter, DestructuringAssignmentTarget[] arguments, Value<?>[] passedArguments) throws AbruptCompletion {
		// Declare passed arguments as variables
		int i = 0;
		for (; i < arguments.length && i < passedArguments.length; i++)
			arguments[i].declare(interpreter, VariableDeclaration.Kind.Let, passedArguments[i]);
		for (; i < arguments.length; i++)
			arguments[i].declare(interpreter, VariableDeclaration.Kind.Let, Undefined.instance);
	}

	String name();

	DestructuringAssignmentTarget[] arguments();

	BlockStatement body();

	default String toCallString() {
		final String name = name();
		final DestructuringAssignmentTarget[] arguments = arguments();

		final var representation = new StringRepresentation();
		if (name != null) representation.append(name);

		representation.append('(');
		if (arguments.length > 0) {
			representation.append(arguments[0]);
			for (int i = 1; i < arguments.length; i++) {
				representation.append(", ");
				arguments[i].represent(representation);
			}
		}

		representation.append(')');
		return representation.toString();
	}

	default Value<?> executeBody(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		try {
			FunctionNode.declareArguments(interpreter, arguments(), passedArguments);
			body().executeWithoutNewContext(interpreter);
			return Undefined.instance;
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	default void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.children("Arguments", arguments())
			.child("Body", body());
	}
}