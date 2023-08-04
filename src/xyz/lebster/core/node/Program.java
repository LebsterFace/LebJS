package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.statement.Statement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

import java.util.List;

public record Program(SourceRange range, List<Statement> children) implements ASTNode {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (final ASTNode child : children) {
			lastValue = child.execute(interpreter);
		}

		return lastValue;
	}
}