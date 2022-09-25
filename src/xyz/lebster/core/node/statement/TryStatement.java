package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.value.Value;

import java.util.Objects;

public record TryStatement(BlockStatement body, String catchParameter, BlockStatement catchBody, BlockStatement finallyBody) implements Statement {
	@Override
	public void dump(int indent) {
		final var builder = DumpBuilder.begin(indent)
			.self(this)
			.child("Body", body);

		Dumper.dumpIndicator(indent + 1, "Catch");
		Dumper.dumpParameterized(indent + 2, "CatchClause", Objects.requireNonNullElse(catchParameter, "No Parameter"));
		catchBody.dump(indent + 3);
		builder.child("Finally", finallyBody);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		try {
			return body.execute(interpreter);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
			if (catchParameter != null)
				interpreter.declareVariable(catchParameter, completion.value);
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