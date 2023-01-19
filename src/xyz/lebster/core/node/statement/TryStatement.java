package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

public record TryStatement(BlockStatement body, StringValue catchParameter, BlockStatement catchBody, BlockStatement finallyBody) implements Statement {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Body", body);

		if (catchBody != null) {
			Dumper.dumpIndicator(indent + 1, "Catch");
			Dumper.dumpValue(indent + 2, "Parameter", catchParameter == null ? "No Parameter" : catchParameter.value);
			catchBody.dump(indent + 2);
		}

		if (finallyBody != null) {
			Dumper.dumpIndicator(indent + 1, "Finally");
			finallyBody.dump(indent + 2);
		}
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