package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

import java.util.List;

public record BlockStatement(SourceRange range, List<Statement> children) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
		try {
			return this.executeWithoutNewContext(interpreter);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	public Value<?> executeWithoutNewContext(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (ASTNode child : children) {
			lastValue = child.execute(interpreter);
		}

		return lastValue;
	}
}