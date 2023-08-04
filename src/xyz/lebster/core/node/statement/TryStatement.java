package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.node.declaration.Kind.Let;

public record TryStatement(SourceRange range, BlockStatement body, StringValue catchParameter, BlockStatement catchBody, BlockStatement finallyBody) implements Statement {
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
}