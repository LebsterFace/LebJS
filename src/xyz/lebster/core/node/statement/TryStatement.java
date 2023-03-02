package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.node.declaration.VariableDeclaration.Kind.Let;

public record TryStatement(BlockStatement body, StringValue catchParameter, BlockStatement catchBody, BlockStatement finallyBody) implements Statement {
	@Override
	public void dump(int indent) {
		final var builder = DumpBuilder.begin(indent)
			.self(this)
			.child("Body", body);
		if (catchBody != null)
			builder.nestedChild("Catch")
				.stringChild("Parameter", catchParameter)
				.child("Body", catchBody);
		if (finallyBody != null)
			builder.nestedChild("Finally")
				.child("Body", finallyBody);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		try {
			return body.execute(interpreter);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
			if (catchParameter != null) {
				interpreter.declareVariable(Let, catchParameter, completion.value);
			}

			try {
				return catchBody.executeWithoutNewContext(interpreter);
			} finally {
				interpreter.exitExecutionContext(context);
			}
		} finally {
			if (finallyBody != null) finallyBody.execute(interpreter);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("try ");
		body.represent(representation);
		representation.append(' ');
		representation.append("catch (");
		representation.append(catchParameter);
		representation.append(") ");
		catchBody.represent(representation);
	}
}