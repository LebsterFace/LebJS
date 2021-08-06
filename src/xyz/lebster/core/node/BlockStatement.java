package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public record BlockStatement(List<Statement> children) implements Statement {
	public BlockStatement() {
		this(new ArrayList<>());
	}

	public BlockStatement append(Statement node) {
		this.children.add(node);
		return this;
	}

	public BlockStatement append(Statement... nodes) {
		this.children.addAll(Arrays.asList(nodes));
		return this;
	}

	public BlockStatement append(Collection<Statement> nodes) {
		this.children.addAll(nodes);
		return this;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushExecutionContext(interpreter.thisValue());
		try {
			Value<?> lastValue = Undefined.instance;
			for (ASTNode child : children) lastValue = child.execute(interpreter);
			return lastValue;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.indent();
		representation.appendLine("{");

		for (ASTNode child : children) {
			representation.appendIndent();
			child.represent(representation);
		}

		representation.unindent();
		representation.appendIndent();
		representation.appendLine("}");
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BlockStatement");
		for (final ASTNode child : children) {
			child.dump(indent + 1);
		}
	}
}