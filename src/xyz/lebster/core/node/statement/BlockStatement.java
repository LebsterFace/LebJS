package xyz.lebster.core.node.statement;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.AppendableNode;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.List;

public record BlockStatement(List<Statement> children) implements Statement, AppendableNode {
	public BlockStatement() {
		this(new ArrayList<>());
	}

	public void append(Statement node) {
		this.children.add(node);
	}

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
		for (ASTNode child : children)
			lastValue = child.execute(interpreter);
		return lastValue;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.indent();
		representation.append("{");
		representation.append('\n');

		for (ASTNode child : children) {
			representation.appendIndent();
			child.represent(representation);
			representation.append('\n');
		}

		representation.unindent();
		representation.appendIndent();
		representation.append('}');
	}
}