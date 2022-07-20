package xyz.lebster.core.node;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public interface FunctionNode extends ASTNode {
	String name();

	FunctionParameters parameters();

	BlockStatement body();

	default String toCallString() {
		final String name = name();
		final var representation = new StringRepresentation();
		if (name != null) representation.append(name);

		representation.append('(');
		parameters().represent(representation);
		representation.append(')');
		return representation.toString();
	}

	default Value<?> executeBody(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushNewEnvironment();
		try {
			parameters().declareArguments(interpreter, passedArguments);
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
			.child("Arguments", parameters())
			.child("Body", body());
	}
}